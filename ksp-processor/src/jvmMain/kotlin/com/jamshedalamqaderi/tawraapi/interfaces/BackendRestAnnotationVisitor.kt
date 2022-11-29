package com.jamshedalamqaderi.tawraapi.interfaces

import com.google.devtools.ksp.symbol.KSClassDeclaration

interface BackendRestAnnotationVisitor {
    fun visit(codeAppender: BackendCodeAppender, classDeclaration: KSClassDeclaration)
}