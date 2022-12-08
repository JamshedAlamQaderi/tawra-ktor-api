package com.jamshedalamqaderi.tawraktorapi.visitors.rest

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.Utils
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSClassDeclarationExtensions.findAllFunctionsAnnotatedWith
import com.jamshedalamqaderi.tawraktorapi.utils.ext.toCamelCase

class BackendRestVisitorImpl(
    private val logger: KSPLogger
) : TawraVisitor<BackendCodeAppender, KSClassDeclaration, Unit> {

    @OptIn(KspExperimental::class)
    override fun visit(appender: BackendCodeAppender, declaration: KSClassDeclaration) {
        // add route block inside the function
        val routePath = declaration.getAnnotationsByType(Rest::class).first()
        appender.addRoute(routePath.path) {
            // process child statement
            val classObjectRef = declaration.simpleName.asString().toCamelCase()
            appender.addStatement("val $classObjectRef = ${declaration.qualifiedName?.asString()}()")
            val annotatedFunctions = declaration.findAllFunctionsAnnotatedWith(
                Utils.listOfRequestMethodAnnotations
            )
            if (annotatedFunctions.isEmpty()) return@addRoute
            val requestMethodVisitor: TawraVisitor<BackendCodeAppender, KSFunctionDeclaration, Unit> =
                BackendRequestMethodVisitorImpl(logger, classObjectRef)
            appender.addImport("io.ktor.server.application", listOf("call"))
            appender.addImport("io.ktor.server.response", listOf("respond"))
            annotatedFunctions.forEach { requestMethodVisitor.visit(appender, it) }
        }
    }
}