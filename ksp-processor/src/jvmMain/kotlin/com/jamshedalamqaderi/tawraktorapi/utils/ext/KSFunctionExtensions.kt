package com.jamshedalamqaderi.tawraktorapi.utils.ext

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.jamshedalamqaderi.tawraktorapi.interfaces.CodeAppender
import kotlin.reflect.KClass

object KSFunctionExtensions {
    fun KSFunctionDeclaration.callFunction(
        codeAppender: CodeAppender,
        classObjectRef: String,
        block: () -> Unit
    ) {
        codeAppender.addStatement("call.respond(")
        codeAppender.addStatement("$classObjectRef.${simpleName.asString()}(")
        block()
        codeAppender.addStatement(")")
        codeAppender.addStatement(")")
    }

    @OptIn(KspExperimental::class)
    fun <T : Annotation> List<KSFunctionDeclaration>.findAnnotatedFunction(annotation: KClass<T>): KSFunctionDeclaration? {
        return this.find {
            it.isAnnotationPresent(annotation)
        }
    }
}
