package com.jamshedalamqaderi.tawraktorapi.interfaces

import com.google.devtools.ksp.symbol.KSClassDeclaration

abstract class TawraSymbolProcessor {
    protected val restSymbols = arrayListOf<KSClassDeclaration>()
    protected val websocketSymbols = arrayListOf<KSClassDeclaration>()

    fun addRestSymbols(symbols: List<KSClassDeclaration>) {
        restSymbols.addAll(symbols)
    }

    fun addWebsocketSymbols(symbols: List<KSClassDeclaration>) {
        websocketSymbols.addAll(symbols)
    }

    abstract fun process(codeGenerator: TawraApiCodeGenerator)
}