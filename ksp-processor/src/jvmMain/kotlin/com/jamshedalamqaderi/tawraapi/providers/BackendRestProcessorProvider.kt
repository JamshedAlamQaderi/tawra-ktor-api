package com.jamshedalamqaderi.tawraapi.providers

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.jamshedalamqaderi.tawraapi.processors.BackendProcessor
import com.jamshedalamqaderi.tawraapi.utils.io.TawraApiCodeGeneratorImpl

class BackendRestProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return BackendProcessor(
            environment.logger,
            TawraApiCodeGeneratorImpl(environment.options)
        )
    }
}