package com.jamshedalamqaderi.tawraapi.appenders

import com.jamshedalamqaderi.tawraapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraapi.interfaces.TawraApiCodeGenerator
import com.squareup.kotlinpoet.FileSpec
import io.ktor.client.HttpClient

class ClientCodeAppenderImpl(
    private val packageName: String,
    private val filename: String,
) : ClientCodeAppender {
    private val fileSpecBuilder =
        FileSpec.scriptBuilder(packageName = packageName, fileName = filename)

    override fun addExtensionProperty(propertyName: String, qualifiedReturnType: String) {
        addCode(
            """
            val ${HttpClient::class.qualifiedName}.$propertyName
                get() = $qualifiedReturnType.getInstance(this)
        """.trimIndent()
        )
    }

    override fun createClass(name: String, block: () -> Unit) {
        addImport("io.ktor.client", listOf("HttpClient"))
        addCode(
            """
            class $name(private val client: HttpClient){
                companion object {
                    private var instance: $name? = null

                    fun getInstance(client: HttpClient): $name {
                        if (instance == null) {
                            instance = $name(client)
                        }
                        return instance!!
                    }
                }
        """.trimIndent()
        )
        block()
        addCode("\n}")
    }

    override fun addImport(packageName: String, names: Iterable<String>) {
        fileSpecBuilder.addImport(packageName, names)
    }

    override fun addCode(code: String, vararg args: Any?) {
        fileSpecBuilder.addCode(code, *args)
    }

    override fun addStatement(code: String, vararg args: Any) {
        fileSpecBuilder.addStatement(code, *args)
    }

    override fun build(): FileSpec {
        return fileSpecBuilder.build()
    }

    override fun writeToFile(codeGenerator: TawraApiCodeGenerator) {
        val outputStream = codeGenerator.createNewCommonModuleFile(
            packageName, filename
        )
        outputStream.write(build().toString().toByteArray())
        outputStream.close()
    }
}