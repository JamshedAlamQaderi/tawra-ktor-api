package com.jamshedalamqaderi.tawraktorapi.utils.ext

import com.google.devtools.ksp.symbol.KSTypeReference

fun String.toCamelCase(): String {
    return this[0].lowercaseChar() + this.substring(1 until this.length)
}

val KSTypeReference.qualifiedType
    get() = resolve().declaration.qualifiedName?.asString()