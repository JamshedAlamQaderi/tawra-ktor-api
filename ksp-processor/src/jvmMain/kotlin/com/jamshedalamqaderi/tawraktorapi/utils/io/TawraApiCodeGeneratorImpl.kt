package com.jamshedalamqaderi.tawraktorapi.utils.io

import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraApiCodeGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class TawraApiCodeGeneratorImpl(options: Map<String, String>) : TawraApiCodeGenerator {
    companion object {
        const val jvmModulePathKey = "jvm-output-path"
        const val commonModulePathKey = "common-output-path"
        const val outputPackageName = "package"
    }

    private val jvmModuleDir: String = options[jvmModulePathKey]!!
    private val commonModuleDir: String = options[commonModulePathKey]!!
    override val packageName: String =
        options[outputPackageName] ?: "com.jamshedalamqaderi.tawraktorapi"

    override fun createNewJvmModuleFile(
        packageName: String,
        fileName: String,
        extensionName: String
    ): OutputStream {
        return createNewFile(jvmModuleDir, packageName, fileName, extensionName)
    }

    override fun createNewCommonModuleFile(
        packageName: String,
        fileName: String,
        extensionName: String
    ): OutputStream {
        return createNewFile(commonModuleDir, packageName, fileName, extensionName)
    }

    private fun createNewFile(
        modulePath: String,
        packageName: String,
        fileName: String,
        extensionName: String
    ): OutputStream {
        val saveDir = File(modulePath, packageName.replace(".", "/"))
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                throw FileSystemException(saveDir, null, "Couldn't making the directory")
            }
        }
        return FileOutputStream(File(saveDir, "${fileName}.$extensionName"))
    }


}