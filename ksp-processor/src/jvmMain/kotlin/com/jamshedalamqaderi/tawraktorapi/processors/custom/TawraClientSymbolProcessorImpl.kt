package com.jamshedalamqaderi.tawraktorapi.processors.custom

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jamshedalamqaderi.tawraktorapi.appenders.ClientCodeAppenderImpl
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraApiCodeGenerator
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraSymbolProcessor
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.toCamelCase
import com.jamshedalamqaderi.tawraktorapi.visitors.rest.ClientRestVisitorImpl
import com.jamshedalamqaderi.tawraktorapi.visitors.websocket.ClientWebSocketVisitorImpl

class TawraClientSymbolProcessorImpl(
    logger: KSPLogger
) : TawraSymbolProcessor() {
    private val clientRestVisitor: TawraVisitor<ClientCodeAppender, KSClassDeclaration, Unit> =
        ClientRestVisitorImpl(logger)
    private val clientWebSocketVisitor: TawraVisitor<ClientCodeAppender, KSClassDeclaration, Unit> =
        ClientWebSocketVisitorImpl(logger)

    override fun process(codeGenerator: TawraApiCodeGenerator) {
        val clientCodeAppender: ClientCodeAppender =
            ClientCodeAppenderImpl(
                codeGenerator.packageName,
                "TawraKtorClientApi"
            )
        generateClientFunction(
            restSymbols,
            codeGenerator,
            "rest",
            clientRestVisitor,
            clientCodeAppender
        )

        generateClientFunction(
            websocketSymbols,
            codeGenerator,
            "websocket",
            clientWebSocketVisitor,
            clientCodeAppender
        )

        clientCodeAppender.writeToFile(codeGenerator)
    }

    private fun generateClientFunction(
        symbols: List<KSClassDeclaration>,
        codeGenerator: TawraApiCodeGenerator,
        packageSuffix: String,
        visitor: TawraVisitor<ClientCodeAppender, KSClassDeclaration, Unit>,
        clientCodeAppender: ClientCodeAppender
    ) {
        symbols.forEach { ksClassDeclaration ->
            val packageName = codeGenerator.packageName + ".$packageSuffix"
            val className = ksClassDeclaration.simpleName.asString()
            val clientChildCodeAppender: ClientCodeAppender =
                ClientCodeAppenderImpl(packageName, className)
            visitor.visit(clientChildCodeAppender, ksClassDeclaration)
            clientChildCodeAppender.writeToFile(codeGenerator)
            // import generated rest functions
            clientCodeAppender.addExtensionProperty(
                className.toCamelCase(),
                "$packageName.$className"
            )
        }
    }
}
