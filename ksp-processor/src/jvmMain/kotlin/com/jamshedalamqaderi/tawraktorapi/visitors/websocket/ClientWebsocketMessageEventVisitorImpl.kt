package com.jamshedalamqaderi.tawraktorapi.visitors.websocket

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.QueryParam
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.requestParams
import com.jamshedalamqaderi.tawraktorapi.utils.ext.generatePath
import com.jamshedalamqaderi.tawraktorapi.utils.ext.qualifiedType
import com.jamshedalamqaderi.tawraktorapi.utils.ext.toCamelCase

class ClientWebsocketMessageEventVisitorImpl(
    private val logger: KSPLogger,
    private val websocketPath: String,
) : TawraVisitor<ClientCodeAppender, KSFunctionDeclaration, Unit> {
    private val functionParamVisitor: TawraVisitor<ClientCodeAppender, KSValueParameter, Unit> =
        ClientWebsocketFunParamVisitorImpl(logger)

    @OptIn(KspExperimental::class)
    override fun visit(appender: ClientCodeAppender, declaration: KSFunctionDeclaration) {
        val functionName = declaration.simpleName.asString()
        val generatedPathTemplate =
            declaration.parameters.requestParams().generatePath(websocketPath)
        appender.addImport(
            "io.ktor.client.plugins.websocket",
            listOf(
                "webSocket",
                "DefaultClientWebSocketSession",
                "receiveDeserialized",
                "webSocketSession",
                "sendSerialized"
            )
        )
        appender.addImport("kotlinx.coroutines.flow", listOf("flow"))

        appender.addStatement("\nprivate lateinit var session : DefaultClientWebSocketSession")
        appender.addStatement("\nsuspend fun $functionName(")
        declaration.parameters.filter {
            it.isAnnotationPresent(PathParam::class) || it.isAnnotationPresent(
                QueryParam::class
            )
        }.forEach { ksValueParameter ->
            functionParamVisitor.visit(appender, ksValueParameter)
        }
        val returnDataType = declaration.returnType?.qualifiedType ?: "Unit"
        appender.addStatement(") = flow<$returnDataType>{")
        appender.addCode(
            """
            session = client.webSocketSession("$generatedPathTemplate")
            while (true){
                emit(session.receiveDeserialized<$returnDataType>())
            }
            """.trimIndent()
        )
        appender.addStatement("}")

        // generate send function
        appender.addStatement("\n suspend fun send(")
        val paramName =
            declaration.returnType?.resolve()?.declaration?.simpleName?.asString()?.toCamelCase()
                ?: "data"
        appender.addStatement("$paramName : $returnDataType")
        appender.addStatement(") {")
        appender.addStatement("session.sendSerialized($paramName)")
        appender.addStatement("}")
    }
}
