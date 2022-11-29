package com.jamshedalamqaderi.tawraapi.visitors

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jamshedalamqaderi.tawraapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraapi.interfaces.ClientRestAnnotationVisitor
import com.jamshedalamqaderi.tawraapi.interfaces.RequestMethodVisitor
import com.jamshedalamqaderi.tawraapi.utils.Utils
import com.jamshedalamqaderi.tawraapi.utils.ext.KSClassDeclarationExtensions.findAllFunctionsAnnotatedWith

class ClientRestAnnotationVisitorImpl(
    private val logger: KSPLogger,
) : ClientRestAnnotationVisitor {

    @OptIn(KspExperimental::class)
    override fun visit(codeAppender: ClientCodeAppender, ksClassDeclaration: KSClassDeclaration) {
        codeAppender.createClass(ksClassDeclaration.simpleName.asString()) {
            val annotatedFunctions = ksClassDeclaration.findAllFunctionsAnnotatedWith(
                Utils.listOfRequestMethodAnnotations
            )
            if (annotatedFunctions.isEmpty()) return@createClass
            val restAnnotation = ksClassDeclaration.getAnnotationsByType(Rest::class).first()
            val clientRequestMethodVisitor: RequestMethodVisitor =
                ClientRequestMethodVisitorImpl(logger, codeAppender, restAnnotation.path)
            annotatedFunctions
                .forEach(clientRequestMethodVisitor::visit)
        }
    }
}