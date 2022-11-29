package com.jamshedalamqaderi.tawraapi.providers

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.jamshedalamqaderi.tawraapi.processors.ClientProcessor
import com.jamshedalamqaderi.tawraapi.utils.io.TawraApiCodeGeneratorImpl

class ClientRestProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ClientProcessor(
            environment.logger,
            TawraApiCodeGeneratorImpl(environment.options)
        )
    }
}