package com.jamshedalamqaderi.tawraktorapi.processors

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Websocket
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraApiCodeGenerator
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraSymbolProcessor
import com.jamshedalamqaderi.tawraktorapi.processors.custom.TawraBackendSymbolProcessorImpl
import com.jamshedalamqaderi.tawraktorapi.processors.custom.TawraClientSymbolProcessorImpl
import com.jamshedalamqaderi.tawraktorapi.utils.ext.findClassesWithAnnotation

class TawraKtorApiProcessor(
    logger: KSPLogger,
    private val codeGenerator: TawraApiCodeGenerator
) : SymbolProcessor {
    private val backendSymbolProcessor: TawraSymbolProcessor =
        TawraBackendSymbolProcessorImpl(logger)
    private val clientSymbolProcessor: TawraSymbolProcessor = TawraClientSymbolProcessorImpl(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // search for Rest annotation
        val restClasses = resolver.findClassesWithAnnotation<Rest>()
        // search for Websocket annotation
        val websocketClasses = resolver.findClassesWithAnnotation<Websocket>()

        backendSymbolProcessor.apply {
            addRestSymbols(restClasses)
            addWebsocketSymbols(websocketClasses)
        }.process(codeGenerator)

        clientSymbolProcessor.apply {
            addRestSymbols(restClasses)
            addWebsocketSymbols(websocketClasses)
        }.process(codeGenerator)

        return emptyList()
    }
}
