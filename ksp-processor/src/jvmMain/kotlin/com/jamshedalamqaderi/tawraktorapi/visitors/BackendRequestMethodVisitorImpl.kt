package com.jamshedalamqaderi.tawraktorapi.visitors

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.jamshedalamqaderi.tawraktorapi.api.RequestDataHandler
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Delete
import com.jamshedalamqaderi.tawraktorapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Get
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Patch
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Post
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Put
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.RequestMethodVisitor
import com.jamshedalamqaderi.tawraktorapi.interfaces.RequestParamVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.AnnotationExtensions.findAnnotatedWith
import com.jamshedalamqaderi.tawraktorapi.utils.ext.AnnotationExtensions.isAnnotationExists
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSFunctionExtensions.callFunction

class BackendRequestMethodVisitorImpl(
    private val logger: KSPLogger,
    private val codeAppender: BackendCodeAppender,
    private val classObjectRef: String
) : RequestMethodVisitor {
    private val backendRequestParamVisitor: RequestParamVisitor<Unit> =
        BackendRequestParamVisitorImpl(logger, codeAppender)

    @OptIn(KspExperimental::class)
    override fun visit(
        ksFunctionDeclaration: KSFunctionDeclaration
    ) {
        if (ksFunctionDeclaration.isAnnotationPresent(Get::class)) {
            val getMethod = ksFunctionDeclaration.getAnnotationsByType(Get::class).first()
            codeAppender.addGet(getMethod.path) {
                callApiFunctionToRespond(ksFunctionDeclaration)
            }
        } else if (ksFunctionDeclaration.isAnnotationPresent(Post::class)) {
            val postMethod = ksFunctionDeclaration.getAnnotationsByType(Post::class).first()
            codeAppender.addPost(postMethod.path) {
                callApiFunctionToRespond(ksFunctionDeclaration)
            }
        } else if (ksFunctionDeclaration.isAnnotationPresent(Put::class)) {
            val putMethod = ksFunctionDeclaration.getAnnotationsByType(Put::class).first()
            codeAppender.addPut(putMethod.path) {
                callApiFunctionToRespond(ksFunctionDeclaration)
            }
        } else if (ksFunctionDeclaration.isAnnotationPresent(Patch::class)) {
            val patchMethod = ksFunctionDeclaration.getAnnotationsByType(Patch::class).first()
            codeAppender.addPatch(patchMethod.path) {
                callApiFunctionToRespond(ksFunctionDeclaration)
            }
        } else if (ksFunctionDeclaration.isAnnotationPresent(Delete::class)) {
            val deleteMethod = ksFunctionDeclaration.getAnnotationsByType(Delete::class).first()
            codeAppender.addDelete(deleteMethod.path) {
                callApiFunctionToRespond(ksFunctionDeclaration)
            }
        } else {
            //todo: throw UnknownHttpMethodException
        }
    }

    private fun callApiFunctionToRespond(
        ksFunctionDeclaration: KSFunctionDeclaration
    ) {
        addStatementForParamAnnotations(ksFunctionDeclaration)
        ksFunctionDeclaration.callFunction(codeAppender, classObjectRef) {
            ksFunctionDeclaration.parameters.forEach { ksValueParameter ->
                backendRequestParamVisitor.visit(ksValueParameter)
            }
        }
    }

    @OptIn(KspExperimental::class)
    private fun addStatementForParamAnnotations(ksFunctionDeclaration: KSFunctionDeclaration) {
        if (ksFunctionDeclaration.parameters.isAnnotationExists<FormParam>()) {
            codeAppender.addImport("io.ktor.server.request", listOf("receiveParameters"))
            codeAppender.addStatement("val formParameters = call.receiveParameters()")
        }
        if (ksFunctionDeclaration.parameters.isAnnotationExists<MultipartForm>()) {
            codeAppender.addImport("io.ktor.server.request", listOf("receiveMultipart"))
            val multipartFormAnno =
                ksFunctionDeclaration.parameters.findAnnotatedWith<MultipartForm>()
                    ?.getAnnotationsByType(MultipartForm::class)?.first()
            codeAppender.addStatement("\nval multipartData = call.receiveMultipart()")
            codeAppender.addStatement(
                "\nval multipartMap = ${RequestDataHandler::class.qualifiedName}.handleMultipartData(%P, multipartData)",
                multipartFormAnno?.uploadPath!!,
            )
        }
    }
}