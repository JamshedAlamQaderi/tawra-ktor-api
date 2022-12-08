package com.jamshedalamqaderi.tawraktorapi.visitors.websocket

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.nullOperator
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.qualifiedType

class ClientWebsocketFunParamVisitorImpl(
    private val logger: KSPLogger
) : TawraVisitor<ClientCodeAppender, KSValueParameter, Unit> {

    override fun visit(appender: ClientCodeAppender, declaration: KSValueParameter) {
        appender.addCode("${declaration.name?.asString()} : ${declaration.qualifiedType()}${declaration.nullOperator()},")
    }
}