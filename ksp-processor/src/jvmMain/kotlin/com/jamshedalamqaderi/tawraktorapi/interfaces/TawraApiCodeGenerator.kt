package com.jamshedalamqaderi.tawraktorapi.interfaces

import java.io.OutputStream

interface TawraApiCodeGenerator {
    val packageName: String
    fun createNewJvmModuleFile(
        packageName: String,
        fileName: String,
        extensionName: String = "kt"
    ): OutputStream

    fun createNewCommonModuleFile(
        packageName: String,
        fileName: String,
        extensionName: String = "kt"
    ): OutputStream
}