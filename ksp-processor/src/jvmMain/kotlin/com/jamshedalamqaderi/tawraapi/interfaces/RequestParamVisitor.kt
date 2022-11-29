package com.jamshedalamqaderi.tawraapi.interfaces

import com.google.devtools.ksp.symbol.KSValueParameter

interface RequestParamVisitor<R> {
    fun visit(ksValueParameter: KSValueParameter): R
}