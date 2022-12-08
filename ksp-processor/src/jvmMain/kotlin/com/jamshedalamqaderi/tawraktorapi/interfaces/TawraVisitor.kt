package com.jamshedalamqaderi.tawraktorapi.interfaces

import com.google.devtools.ksp.symbol.KSNode

interface TawraVisitor<T : CodeAppender, D : KSNode, R> {
    fun visit(appender: T, declaration: D): R
}
