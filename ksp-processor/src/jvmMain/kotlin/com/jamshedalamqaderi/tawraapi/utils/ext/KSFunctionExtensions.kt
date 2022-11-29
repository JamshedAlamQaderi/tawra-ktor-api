package com.jamshedalamqaderi.tawraapi.utils.ext

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.jamshedalamqaderi.tawraapi.interfaces.CodeAppender

object KSFunctionExtensions {
    fun KSFunctionDeclaration.callFunction(
        codeAppender: CodeAppender,
        classObjectRef: String,
        block: () -> Unit
    ) {
        codeAppender.addStatement("call.respond(")
        codeAppender.addStatement("${classObjectRef}.${simpleName.asString()}(")
        block()
        codeAppender.addStatement(")")
        codeAppender.addStatement(")")
    }
}