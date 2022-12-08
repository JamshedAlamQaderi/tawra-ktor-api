package com.jamshedalamqaderi.tawraktorapi.visitors.websocket

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnMessage
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Websocket
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSFunctionExtensions.findAnnotatedFunction

class ClientWebSocketVisitorImpl(
    private val logger: KSPLogger
) : TawraVisitor<ClientCodeAppender, KSClassDeclaration, Unit> {

    @OptIn(KspExperimental::class)
    override fun visit(appender: ClientCodeAppender, declaration: KSClassDeclaration) {
        appender.createClass(declaration.simpleName.asString()) {
            val messageFunction =
                declaration
                    .getAllFunctions()
                    .toList()
                    .findAnnotatedFunction(OnMessage::class)
            if (messageFunction != null) {
                val eventVisitor: TawraVisitor<ClientCodeAppender, KSFunctionDeclaration, Unit> =
                    ClientWebsocketMessageEventVisitorImpl(
                        logger,
                        declaration.getAnnotationsByType(Websocket::class).first().path
                    )
                eventVisitor.visit(appender, messageFunction)
            }
        }
    }
}
