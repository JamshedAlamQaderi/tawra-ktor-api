package com.jamshedalamqaderi.tawraktorapi.interfaces

import com.google.devtools.ksp.symbol.KSClassDeclaration

interface ClientRestAnnotationVisitor {
    fun visit(codeAppender: ClientCodeAppender, ksClassDeclaration: KSClassDeclaration)
}
