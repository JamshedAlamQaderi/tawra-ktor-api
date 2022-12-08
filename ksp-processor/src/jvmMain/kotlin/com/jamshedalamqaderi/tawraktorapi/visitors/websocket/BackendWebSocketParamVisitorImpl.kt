package com.jamshedalamqaderi.tawraktorapi.visitors.websocket

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.processPathParamAnnotation
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.processQueryParamAnnotation
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.processWebsocketBodyAnnotation
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.qualifiedType
import io.ktor.server.websocket.WebSocketServerSession

class BackendWebSocketParamVisitorImpl(
    private val logger: KSPLogger
) : TawraVisitor<BackendCodeAppender, KSValueParameter, Unit> {

    override fun visit(appender: BackendCodeAppender, declaration: KSValueParameter) {
        if (declaration.qualifiedType() == WebSocketServerSession::class.qualifiedName
        ) {
            appender.addStatement("this,")
            return
        } else if (declaration.qualifiedType() == Exception::class.qualifiedName ||
            declaration.qualifiedType() == Throwable::class.qualifiedName
        ) {
            appender.addStatement("e,")
            return
        }
        declaration.processPathParamAnnotation(appender)
        declaration.processQueryParamAnnotation(appender)
        declaration.processWebsocketBodyAnnotation(appender)
    }
}
