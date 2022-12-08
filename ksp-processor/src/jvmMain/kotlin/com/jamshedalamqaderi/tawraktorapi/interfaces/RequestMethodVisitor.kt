package com.jamshedalamqaderi.tawraktorapi.interfaces

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

interface RequestMethodVisitor {
    fun visit(
        ksFunctionDeclaration: KSFunctionDeclaration
    )
}
