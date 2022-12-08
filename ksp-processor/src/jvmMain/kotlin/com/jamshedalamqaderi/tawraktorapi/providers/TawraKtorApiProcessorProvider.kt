package com.jamshedalamqaderi.tawraktorapi.providers

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.jamshedalamqaderi.tawraktorapi.processors.TawraKtorApiProcessor
import com.jamshedalamqaderi.tawraktorapi.utils.io.TawraApiCodeGeneratorImpl

class TawraKtorApiProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return TawraKtorApiProcessor(
            environment.logger,
            TawraApiCodeGeneratorImpl(environment.options)
        )
    }
}
