package com.jamshedalamqaderi.tawraktorapi.visitors.rest

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.api.RequestDataHandler
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Delete
import com.jamshedalamqaderi.tawraktorapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Get
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Patch
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Post
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Put
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.AnnotationExtensions.findAnnotatedWith
import com.jamshedalamqaderi.tawraktorapi.utils.ext.AnnotationExtensions.isAnnotationExists
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSFunctionExtensions.callFunction

class BackendRequestMethodVisitorImpl(
    private val logger: KSPLogger,
    private val classObjectRef: String
) : TawraVisitor<BackendCodeAppender, KSFunctionDeclaration, Unit> {
    private val paramVisitor: TawraVisitor<BackendCodeAppender, KSValueParameter, Unit> =
        BackendRequestParamVisitorImpl(logger)

    @OptIn(KspExperimental::class)
    override fun visit(
        appender: BackendCodeAppender,
        declaration: KSFunctionDeclaration
    ) {
        if (declaration.isAnnotationPresent(Get::class)) {
            val getMethod = declaration.getAnnotationsByType(Get::class).first()
            appender.addGet(getMethod.path) {
                callApiFunctionToRespond(appender, declaration)
            }
        } else if (declaration.isAnnotationPresent(Post::class)) {
            val postMethod = declaration.getAnnotationsByType(Post::class).first()
            appender.addPost(postMethod.path) {
                callApiFunctionToRespond(appender, declaration)
            }
        } else if (declaration.isAnnotationPresent(Put::class)) {
            val putMethod = declaration.getAnnotationsByType(Put::class).first()
            appender.addPut(putMethod.path) {
                callApiFunctionToRespond(appender, declaration)
            }
        } else if (declaration.isAnnotationPresent(Patch::class)) {
            val patchMethod = declaration.getAnnotationsByType(Patch::class).first()
            appender.addPatch(patchMethod.path) {
                callApiFunctionToRespond(appender, declaration)
            }
        } else if (declaration.isAnnotationPresent(Delete::class)) {
            val deleteMethod = declaration.getAnnotationsByType(Delete::class).first()
            appender.addDelete(deleteMethod.path) {
                callApiFunctionToRespond(appender, declaration)
            }
        } else {
            // todo: throw UnknownHttpMethodException
        }
    }

    private fun callApiFunctionToRespond(
        codeAppender: BackendCodeAppender,
        ksFunctionDeclaration: KSFunctionDeclaration
    ) {
        addStatementForParamAnnotations(codeAppender, ksFunctionDeclaration)
        ksFunctionDeclaration.callFunction(codeAppender, classObjectRef) {
            ksFunctionDeclaration.parameters.forEach { ksValueParameter ->
                paramVisitor.visit(codeAppender, ksValueParameter)
            }
        }
    }

    @OptIn(KspExperimental::class)
    private fun addStatementForParamAnnotations(
        codeAppender: BackendCodeAppender,
        ksFunctionDeclaration: KSFunctionDeclaration
    ) {
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
