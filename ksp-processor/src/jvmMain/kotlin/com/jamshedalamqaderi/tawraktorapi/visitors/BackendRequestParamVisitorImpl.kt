package com.jamshedalamqaderi.tawraktorapi.visitors

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Body
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Channel
import com.jamshedalamqaderi.tawraktorapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.QueryParam
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.RequestParamVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.constructorParams
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.convertParameterToType
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.nullOperator
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.qualifiedType
import com.jamshedalamqaderi.tawraktorapi.utils.ext.qualifiedType

@OptIn(KspExperimental::class)
class BackendRequestParamVisitorImpl(
    private val logger: KSPLogger,
    private val codeAppender: BackendCodeAppender,
) : RequestParamVisitor<Unit> {


    override fun visit(ksValueParameter: KSValueParameter) {
        processPathParamAnnotation(ksValueParameter)
        processQueryParamAnnotation(ksValueParameter)
        processFormParamAnnotation(ksValueParameter)
        processBodyAnnotation(ksValueParameter)
        processMultipartFormAnnotation(ksValueParameter)
        processChannelAnnotation(ksValueParameter)
    }

    private fun processPathParamAnnotation(ksValueParameter: KSValueParameter) {
        if (!ksValueParameter.isAnnotationPresent(PathParam::class)) return
        val pathParam = ksValueParameter.getAnnotationsByType(PathParam::class).first()
        codeAppender.addStatement(
            "call.parameters[%P]${ksValueParameter.convertParameterToType()},",
            pathParam.name.ifEmpty { ksValueParameter.name?.asString() }!!
        )
    }

    private fun processQueryParamAnnotation(ksValueParameter: KSValueParameter) {
        if (!ksValueParameter.isAnnotationPresent(QueryParam::class)) return
        val queryParam = ksValueParameter.getAnnotationsByType(QueryParam::class).first()
        codeAppender.addStatement(
            "call.request.queryParameters[%P]${ksValueParameter.convertParameterToType()},",
            queryParam.name.ifEmpty { ksValueParameter.name?.asString() }!!
        )
    }

    private fun processFormParamAnnotation(ksValueParameter: KSValueParameter) {
        if (!ksValueParameter.isAnnotationPresent(FormParam::class)) return
        val formParam = ksValueParameter.getAnnotationsByType(FormParam::class).first()
        codeAppender.addStatement(
            "formParameters[%P]${ksValueParameter.convertParameterToType()},",
            formParam.name.ifEmpty { ksValueParameter.name?.asString() }!!
        )
    }

    private fun processBodyAnnotation(ksValueParameter: KSValueParameter) {
        if (!ksValueParameter.isAnnotationPresent(Body::class)) return
        codeAppender.addImport("io.ktor.server.request", listOf("receive"))
        codeAppender.addStatement("call.receive<${ksValueParameter.qualifiedType()}>(),")
    }

    private fun processMultipartFormAnnotation(ksValueParameter: KSValueParameter) {
        if (!ksValueParameter.isAnnotationPresent(MultipartForm::class)) return
        val params = ksValueParameter.constructorParams()
        codeAppender.addStatement("${ksValueParameter.qualifiedType()}(")
        params.forEach { param ->
            codeAppender.addStatement(
                "multipartMap[%P] as ${param.type.qualifiedType}${param.nullOperator()},",
                param.name?.asString()!!
            )
        }
        codeAppender.addStatement(")")
    }

    private fun processChannelAnnotation(ksValueParameter: KSValueParameter) {
        if (!ksValueParameter.isAnnotationPresent(Channel::class)) return
        codeAppender.addImport("io.ktor.server.request", listOf("receiveChannel"))
        codeAppender.addStatement("call.receiveChannel(),")

    }
}