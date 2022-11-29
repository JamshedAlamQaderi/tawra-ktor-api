package com.jamshedalamqaderi.tawraapi.processors

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jamshedalamqaderi.tawraapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraapi.appenders.ClientCodeAppenderImpl
import com.jamshedalamqaderi.tawraapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraapi.interfaces.ClientRestAnnotationVisitor
import com.jamshedalamqaderi.tawraapi.interfaces.TawraApiCodeGenerator
import com.jamshedalamqaderi.tawraapi.utils.ext.toCamelCase
import com.jamshedalamqaderi.tawraapi.visitors.ClientRestAnnotationVisitorImpl
import com.squareup.kotlinpoet.FileSpec

class ClientProcessor(
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
            val clientCodeAppender: ClientCodeAppender =
                ClientCodeAppenderImpl(
                    codeGenerator.packageName,
                    "TawraKtorClientApi"
                )
            val clientRestVisitor: ClientRestAnnotationVisitor =
                ClientRestAnnotationVisitorImpl(logger)
            restSymbols.forEach { ksClassDeclaration ->
                val packageName = codeGenerator.packageName + ".rest"
                val className = ksClassDeclaration.simpleName.asString()
                val restClientCodeAppender: ClientCodeAppender =
                    ClientCodeAppenderImpl(packageName, className)
                clientRestVisitor.visit(restClientCodeAppender, ksClassDeclaration)
                restClientCodeAppender.writeToFile(codeGenerator)
                // import generated rest functions
                clientCodeAppender.addExtensionProperty(
                    className.toCamelCase(),
                    "$packageName.$className"
                )
            }
            clientCodeAppender.writeToFile(codeGenerator)
        }
        return emptyList()
    }
}