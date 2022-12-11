package com.jamshedalamqaderi.tawraktorapi.visitors.websocket

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnMessage
import com.jamshedalamqaderi.tawraktorapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.QueryParam
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.AnnotationExtensions.findAnnotatedWith
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.requestParams
import com.jamshedalamqaderi.tawraktorapi.utils.ext.generatePath

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
        val onMessageAnno = declaration.annotations.findAnnotatedWith<OnMessage>()
        val returnDataType =
            (onMessageAnno?.arguments?.first()?.value as KSType?)?.declaration?.qualifiedName?.asString()
                ?: "Unit"
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
        val paramName = "data"
        appender.addStatement("$paramName : $returnDataType")
        appender.addStatement(") {")
        appender.addStatement("session.sendSerialized($paramName)")
        appender.addStatement("}")
    }
}
