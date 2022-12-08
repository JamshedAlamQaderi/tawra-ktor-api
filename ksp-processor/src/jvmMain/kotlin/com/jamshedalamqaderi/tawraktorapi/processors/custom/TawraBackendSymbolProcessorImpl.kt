package com.jamshedalamqaderi.tawraktorapi.processors.custom

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jamshedalamqaderi.tawraktorapi.appenders.BackendCodeAppenderImpl
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraApiCodeGenerator
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraSymbolProcessor
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.toCamelCase
import com.jamshedalamqaderi.tawraktorapi.visitors.rest.BackendRestVisitorImpl
import com.jamshedalamqaderi.tawraktorapi.visitors.websocket.BackendWebSocketVisitorImpl

class TawraBackendSymbolProcessorImpl(
    logger: KSPLogger
) : TawraSymbolProcessor() {

    private val backendRestVisitor: TawraVisitor<BackendCodeAppender, KSClassDeclaration, Unit> =
        BackendRestVisitorImpl(logger)
    private val backendWebSocketVisitor: TawraVisitor<BackendCodeAppender, KSClassDeclaration, Unit> =
        BackendWebSocketVisitorImpl(logger)

    override fun process(codeGenerator: TawraApiCodeGenerator) {
        val backendCodeAppender = BackendCodeAppenderImpl(
            codeGenerator.packageName,
            "TawraKtorApi",
            "tawraKtorApiRoutes"
        )

        generateFunction(
            restSymbols,
            codeGenerator,
            "rest",
            backendCodeAppender,
            backendRestVisitor,
        )

        generateFunction(
            websocketSymbols,
            codeGenerator,
            "websocket",
            backendCodeAppender,
            backendWebSocketVisitor
        )

        backendCodeAppender.writeToFile(codeGenerator)
    }

    private fun generateFunction(
        symbols: List<KSClassDeclaration>,
        codeGenerator: TawraApiCodeGenerator,
        packageSuffix: String,
        backendCodeAppender: BackendCodeAppender,
        visitor: TawraVisitor<BackendCodeAppender, KSClassDeclaration, Unit>,
    ) {
        symbols.forEach { ksClassDeclaration ->
            val packageName = codeGenerator.packageName + ".$packageSuffix"
            val filename = ksClassDeclaration.simpleName.asString()
            val functionName = filename.toCamelCase()
            val backendChildCodeAppender: BackendCodeAppender =
                BackendCodeAppenderImpl(packageName, filename, functionName)
            visitor.visit(backendChildCodeAppender, ksClassDeclaration)
            // generate individual files for each class
            backendChildCodeAppender.writeToFile(codeGenerator)
            // import generated websocket function
            backendCodeAppender.addImport(packageName, listOf(functionName))
            backendCodeAppender.addStatement("${functionName}()")
        }
    }
}