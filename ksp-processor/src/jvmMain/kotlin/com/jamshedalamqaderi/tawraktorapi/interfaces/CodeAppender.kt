package com.jamshedalamqaderi.tawraktorapi.interfaces

import com.squareup.kotlinpoet.FileSpec

interface CodeAppender {
    fun addImport(packageName: String, names: Iterable<String>)
    fun addCode(code: String, vararg args: Any?)
    fun addStatement(code: String, vararg args: Any)
    fun build(): FileSpec
    fun writeToFile(codeGenerator: TawraApiCodeGenerator)
}