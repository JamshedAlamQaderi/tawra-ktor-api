package com.jamshedalamqaderi.tawraapi.visitors

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jamshedalamqaderi.tawraapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraapi.interfaces.BackendRestAnnotationVisitor
import com.jamshedalamqaderi.tawraapi.interfaces.RequestMethodVisitor
import com.jamshedalamqaderi.tawraapi.utils.Utils
import com.jamshedalamqaderi.tawraapi.utils.ext.KSClassDeclarationExtensions.findAllFunctionsAnnotatedWith
import com.jamshedalamqaderi.tawraapi.utils.ext.toCamelCase

class BackendRestAnnotationVisitorImpl(
    private val logger: KSPLogger
) :
    BackendRestAnnotationVisitor {

    @OptIn(KspExperimental::class)
    override fun visit(codeAppender: BackendCodeAppender, classDeclaration: KSClassDeclaration) {
        // add route block inside the function
        val routePath = classDeclaration.getAnnotationsByType(Rest::class).first()
        codeAppender.addRoute(routePath.path) {
            // process child statement
            val classObjectRef = classDeclaration.simpleName.asString().toCamelCase()
            codeAppender.addStatement("\tval $classObjectRef = ${classDeclaration.qualifiedName?.asString()}()")
            val annotatedFunctions = classDeclaration.findAllFunctionsAnnotatedWith(
                Utils.listOfRequestMethodAnnotations
            )
            if (annotatedFunctions.isEmpty()) return@addRoute
            val requestMethodVisitor: RequestMethodVisitor =
                BackendRequestMethodVisitorImpl(logger, codeAppender, classObjectRef)
            codeAppender.addImport("io.ktor.server.application", listOf("call"))
            codeAppender.addImport("io.ktor.server.response", listOf("respond"))
            annotatedFunctions.forEach(requestMethodVisitor::visit)
        }
    }
}