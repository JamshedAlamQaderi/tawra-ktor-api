package com.jamshedalamqaderi.tawraktorapi.visitors.rest

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.RequestMethodVisitor
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.Utils
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSClassDeclarationExtensions.findAllFunctionsAnnotatedWith
import com.jamshedalamqaderi.tawraktorapi.visitors.ClientRequestMethodVisitorImpl

class ClientRestVisitorImpl(
    private val logger: KSPLogger,
) : TawraVisitor<ClientCodeAppender, KSClassDeclaration, Unit> {

    @OptIn(KspExperimental::class)
    override fun visit(appender: ClientCodeAppender, declaration: KSClassDeclaration) {
        appender.createClass(declaration.simpleName.asString()) {
            val annotatedFunctions = declaration.findAllFunctionsAnnotatedWith(
                Utils.listOfRequestMethodAnnotations
            )
            if (annotatedFunctions.isEmpty()) return@createClass
            val restAnnotation = declaration.getAnnotationsByType(Rest::class).first()
            val clientRequestMethodVisitor: RequestMethodVisitor =
                ClientRequestMethodVisitorImpl(logger, appender, restAnnotation.path)
            annotatedFunctions
                .forEach(clientRequestMethodVisitor::visit)
        }
    }
}
