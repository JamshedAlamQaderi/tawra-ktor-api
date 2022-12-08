package com.jamshedalamqaderi.tawraktorapi.utils.ext

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.jamshedalamqaderi.tawraktorapi.models.RequestParam
import com.jamshedalamqaderi.tawraktorapi.models.RequestParamType

fun String.toCamelCase(): String {
    return this[0].lowercaseChar() + this.substring(1 until this.length)
}

val KSTypeReference.qualifiedType
    get() = resolve().declaration.qualifiedName?.asString()

inline fun <reified T : Annotation> Resolver.findClassesWithAnnotation(): List<KSClassDeclaration> {
    return this.getSymbolsWithAnnotation(T::class.qualifiedName!!)
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.CLASS }
        .toList()
}

fun List<RequestParam>.generatePath(actualPath: String): String {
    var path = actualPath
    // inject variable to path params
    this.filter { it.paramType == RequestParamType.PATH_PARAM }
        .forEach { param ->
            path = path.replace("{${param.paramKey}}", "$" + param.paramName)
        }
    // inject query param values
    val queries = this
        .filter { it.paramType == RequestParamType.QUERY_PARAM }
        .joinToString("=") { param -> "${param.paramKey}=$${param.paramName}" }
    if (queries.isNotEmpty()) {
        path = "$path?$queries"
    }
    return path
}
