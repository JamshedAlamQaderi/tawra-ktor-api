package com.jamshedalamqaderi.tawraktorapi.processors

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraktorapi.appenders.BackendCodeAppenderImpl
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraApiCodeGenerator
import com.jamshedalamqaderi.tawraktorapi.utils.ext.toCamelCase
import com.jamshedalamqaderi.tawraktorapi.visitors.BackendRestAnnotationVisitorImpl

class BackendProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: TawraApiCodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val restSymbols = resolver
            .getSymbolsWithAnnotation(Rest::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .toList()
        if (restSymbols.isNotEmpty()) {
            val backendCodeAppender =
                BackendCodeAppenderImpl(codeGenerator.packageName, "TawraKtorApi", "tawraKtorApiRoutes")
            val backendRestVisitor = BackendRestAnnotationVisitorImpl(logger)
            restSymbols.forEach { ksClassDeclaration ->
                val packageName = codeGenerator.packageName + ".rest"
                val filename = ksClassDeclaration.simpleName.asString()
                val restFunctionName = filename.toCamelCase()
                val restBackendCodeAppender =
                    BackendCodeAppenderImpl(packageName, filename, restFunctionName)
                backendRestVisitor.visit(restBackendCodeAppender, ksClassDeclaration)
                restBackendCodeAppender.writeToFile(codeGenerator)
                // import generated rest functions
                backendCodeAppender.addImport(packageName, listOf(restFunctionName))
                backendCodeAppender.addStatement("${restFunctionName}()")
            }
            backendCodeAppender.writeToFile(codeGenerator)
        }
        return emptyList()
    }
}