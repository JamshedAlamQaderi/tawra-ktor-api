package com.jamshedalamqaderi.tawraapi.utils.ext

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability

object KSValueParameterExtensions {
    fun KSValueParameter.nullOperator(): String {
        return when (this.type.resolve().nullability) {
            Nullability.NULLABLE -> "?"
            else -> ""
        }
    }

    fun KSValueParameter.nullSafeOperator(): String {
        return when (this.type.resolve().nullability) {
            Nullability.NULLABLE -> ""
            else -> "!!"
        }
    }

    fun KSValueParameter.qualifiedType(): String {
        return type.resolve().declaration.qualifiedName?.asString() ?: "kotlin.Unit"
    }

    fun KSValueParameter.constructorParams(): List<KSValueParameter> {
        return type
            .resolve()
            .declaration
            .closestClassDeclaration()
            ?.primaryConstructor
            ?.parameters
            ?: listOf()
    }

    fun KSValueParameter.convertParameterToType(): String {
        if (qualifiedType() == String::class.qualifiedName) {
            return nullSafeOperator()
        }
        return "?.to${type.resolve().declaration.simpleName.asString()}${orNull()}()${nullSafeOperator()}"
    }

    private fun KSValueParameter.orNull(): String {
        return if (type.resolve().nullability == Nullability.NULLABLE)
            "OrNull"
        else ""
    }
}