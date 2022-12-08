package com.jamshedalamqaderi.tawraktorapi.utils.ext

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Body
import com.jamshedalamqaderi.tawraktorapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.QueryParam
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.models.RequestParam
import com.jamshedalamqaderi.tawraktorapi.models.RequestParamType

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

    @OptIn(KspExperimental::class)
    fun KSValueParameter.processPathParamAnnotation(
        codeAppender: BackendCodeAppender
    ) {
        if (!this.isAnnotationPresent(PathParam::class)) return
        val pathParam = this.getAnnotationsByType(PathParam::class).first()
        codeAppender.addStatement(
            "call.parameters[%P]${this.convertParameterToType()},",
            pathParam.name.ifEmpty { this.name?.asString() }!!
        )
    }


    @OptIn(KspExperimental::class)
    fun KSValueParameter.processQueryParamAnnotation(
        codeAppender: BackendCodeAppender
    ) {
        if (!this.isAnnotationPresent(QueryParam::class)) return
        val queryParam = this.getAnnotationsByType(QueryParam::class).first()
        codeAppender.addStatement(
            "call.request.queryParameters[%P]${this.convertParameterToType()},",
            queryParam.name.ifEmpty { this.name?.asString() }!!
        )
    }

    @OptIn(KspExperimental::class)
    fun KSValueParameter.processWebsocketBodyAnnotation(codeAppender: BackendCodeAppender) {
        if (!this.isAnnotationPresent(Body::class)) return
        codeAppender.addImport("io.ktor.server.websocket", listOf("receiveDeserialized"))
        codeAppender.addStatement("receiveDeserialized<${this.qualifiedType()}>()")
    }

    @OptIn(KspExperimental::class)
    fun List<KSValueParameter>.requestParams(): List<RequestParam> {
        return mapNotNull { ksValueParameter ->
            if (ksValueParameter.isAnnotationPresent(PathParam::class)) {
                val param = ksValueParameter.getAnnotationsByType(PathParam::class).first()
                RequestParam(
                    param.name.ifEmpty { ksValueParameter.name?.asString()!! },
                    ksValueParameter.name?.asString()!!,
                    RequestParamType.PATH_PARAM
                )
            } else if (ksValueParameter.isAnnotationPresent(QueryParam::class)) {
                val param = ksValueParameter.getAnnotationsByType(QueryParam::class).first()
                RequestParam(
                    param.name.ifEmpty { ksValueParameter.name?.asString()!! },
                    ksValueParameter.name?.asString()!!,
                    RequestParamType.QUERY_PARAM
                )
            } else {
                null
            }
        }
    }
}