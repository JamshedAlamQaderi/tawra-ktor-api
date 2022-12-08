package com.jamshedalamqaderi.tawraktorapi.visitors.websocket

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnConnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnDisconnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnError
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnMessage
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import kotlin.reflect.KClass

class BackendWebSocketEventVisitorImpl(
    private val logger: KSPLogger,
    private val classObjectRef: String
) :
    TawraVisitor<BackendCodeAppender, KSFunctionDeclaration, Unit> {
    private val paramVisitor = BackendWebSocketParamVisitorImpl(logger)

    override fun visit(appender: BackendCodeAppender, declaration: KSFunctionDeclaration) {
        fun <T : Annotation> wrapper(annotation: KClass<T>) {
            callReferenceFunction(annotation, appender, declaration) {
                declaration.parameters.forEach { ksValueParameter ->
                    paramVisitor.visit(appender, ksValueParameter)
                }
            }
        }

        wrapper(OnConnected::class)
        wrapper(OnMessage::class)
        wrapper(OnError::class)
        wrapper(OnDisconnected::class)
    }

    @OptIn(KspExperimental::class)
    private fun <T : Annotation> callReferenceFunction(
        annotation: KClass<T>,
        appender: BackendCodeAppender,
        declaration: KSFunctionDeclaration,
        block: () -> Unit
    ) {
        if (declaration.isAnnotationPresent(annotation)) {
            appender.addStatement("$classObjectRef.${declaration.simpleName.asString()}(")
            block()
            appender.addStatement(")")
        }
    }
}
