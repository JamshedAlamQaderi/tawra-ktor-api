package com.jamshedalamqaderi.tawraapi.utils.ext

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kotlin.reflect.KClass

object KSClassDeclarationExtensions {

    fun KSClassDeclaration.findAllFunctionsAnnotatedWith(kClasses: List<KClass<*>>): List<KSFunctionDeclaration> {
        val annotations = kClasses.map { it.qualifiedName }
        return getAllFunctions()
            .filter { ksFunctionDeclaration ->
                ksFunctionDeclaration.annotations.find {
                    annotations.contains(
                        it.annotationType.resolve()
                            .declaration
                            .qualifiedName
                            ?.asString()
                    )
                } != null
            }.toList()
    }
}
