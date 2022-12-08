package com.jamshedalamqaderi.tawraktorapi.visitors.rest

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Body
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Channel
import com.jamshedalamqaderi.tawraktorapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.constructorParams
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.convertParameterToType
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.nullOperator
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.processPathParamAnnotation
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.processQueryParamAnnotation
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.qualifiedType
import com.jamshedalamqaderi.tawraktorapi.utils.ext.qualifiedType

@OptIn(KspExperimental::class)
class BackendRequestParamVisitorImpl(
    private val logger: KSPLogger
) : TawraVisitor<BackendCodeAppender, KSValueParameter, Unit> {

    override fun visit(appender: BackendCodeAppender, declaration: KSValueParameter) {
        declaration.processPathParamAnnotation(appender)
        declaration.processQueryParamAnnotation(appender)
        processFormParamAnnotation(appender, declaration)
        processBodyAnnotation(appender, declaration)
        processMultipartFormAnnotation(appender, declaration)
        processChannelAnnotation(appender, declaration)
    }

    private fun processFormParamAnnotation(
        codeAppender: BackendCodeAppender,
        ksValueParameter: KSValueParameter
    ) {
        if (!ksValueParameter.isAnnotationPresent(FormParam::class)) return
        val formParam = ksValueParameter.getAnnotationsByType(FormParam::class).first()
        codeAppender.addStatement(
            "formParameters[%P]${ksValueParameter.convertParameterToType()},",
            formParam.name.ifEmpty { ksValueParameter.name?.asString() }!!
        )
    }

    fun processBodyAnnotation(
        codeAppender: BackendCodeAppender,
        ksValueParameter: KSValueParameter,
    ) {
        if (!ksValueParameter.isAnnotationPresent(Body::class)) return
        codeAppender.addImport("io.ktor.server.request", listOf("receive"))
        codeAppender.addStatement("call.receive<${ksValueParameter.qualifiedType()}>(),")
    }

    private fun processMultipartFormAnnotation(
        codeAppender: BackendCodeAppender,
        ksValueParameter: KSValueParameter
    ) {
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

    private fun processChannelAnnotation(
        codeAppender: BackendCodeAppender,
        ksValueParameter: KSValueParameter
    ) {
        if (!ksValueParameter.isAnnotationPresent(Channel::class)) return
        codeAppender.addImport("io.ktor.server.request", listOf("receiveChannel"))
        codeAppender.addStatement("call.receiveChannel(),")

    }

}