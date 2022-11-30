package com.jamshedalamqaderi.tawraktorapi.appenders

import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraApiCodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import io.ktor.server.routing.Route

class BackendCodeAppenderImpl(
    private val packageName: String,
    private val filename: String,
    functionName: String
) : BackendCodeAppender {

    private val fileSpecBuilder = FileSpec.builder(packageName, filename)
    private val funSpecBuilder = FunSpec.builder(functionName).receiver(Route::class)

    override fun addRoute(path: String, block: () -> Unit) {
        requestMethodPreset("route", path, block)
    }

    override fun addGet(path: String, block: () -> Unit) {
        requestMethodPreset("get", path, block)
    }

    override fun addPost(path: String, block: () -> Unit) {
        requestMethodPreset("post", path, block)
    }

    override fun addPut(path: String, block: () -> Unit) {
        requestMethodPreset("put", path, block)
    }

    override fun addPatch(path: String, block: () -> Unit) {
        requestMethodPreset("patch", path, block)
    }

    override fun addDelete(path: String, block: () -> Unit) {
        requestMethodPreset("delete", path, block)
    }

    override fun addImport(packageName: String, names: Iterable<String>) {
        fileSpecBuilder.addImport(packageName, names)
    }

    override fun addCode(code: String, vararg args: Any?) {
        funSpecBuilder.addCode(code, *args)
    }

    override fun addStatement(code: String, vararg args: Any) {
        funSpecBuilder.addStatement(code, *args)
    }

    override fun build(): FileSpec {
        return fileSpecBuilder.addFunction(funSpecBuilder.build()).build()
    }

    override fun writeToFile(codeGenerator: TawraApiCodeGenerator) {
        val outputStream = codeGenerator.createNewJvmModuleFile(
            packageName,
            filename
        )
        outputStream.write(build().toString().toByteArray())
        outputStream.close()
    }

    private fun requestMethodPreset(functionName: String, path: String, block: () -> Unit) {
        addImport("io.ktor.server.routing", listOf(functionName))
        addStatement("$functionName(%P){", path)
        block()
        addStatement("}")
    }
}