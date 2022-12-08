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
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.RequestParamVisitor
import com.jamshedalamqaderi.tawraktorapi.models.RequestParam
import com.jamshedalamqaderi.tawraktorapi.models.RequestParamType
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.nullOperator
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.qualifiedType

class ClientRequestParamVisitorImpl(
    private val logger: KSPLogger,
    private val codeAppender: ClientCodeAppender,
) : RequestParamVisitor<RequestParam> {

    @OptIn(KspExperimental::class)
    override fun visit(ksValueParameter: KSValueParameter): RequestParam {
        codeAppender.addCode("${ksValueParameter.name?.asString()} : ${ksValueParameter.qualifiedType()}${ksValueParameter.nullOperator()},")

        if (ksValueParameter.isAnnotationPresent(PathParam::class)) {
            val pathParam = ksValueParameter.getAnnotationsByType(PathParam::class).first()
            return RequestParam(
                pathParam.name.ifEmpty { ksValueParameter.name?.asString()!! },
                ksValueParameter.name?.asString()!!,
                RequestParamType.PATH_PARAM
            )
        } else if (ksValueParameter.isAnnotationPresent(QueryParam::class)) {
            val queryParam = ksValueParameter.getAnnotationsByType(QueryParam::class).first()
            return RequestParam(
                queryParam.name.ifEmpty { ksValueParameter.name?.asString()!! },
                ksValueParameter.name?.asString()!!, RequestParamType.QUERY_PARAM
            )
        } else if (ksValueParameter.isAnnotationPresent(FormParam::class)) {
            val formParam = ksValueParameter.getAnnotationsByType(FormParam::class).first()
            return RequestParam(
                formParam.name.ifEmpty { ksValueParameter.name?.asString()!! },
                ksValueParameter.name?.asString()!!, RequestParamType.FORM_PARAM
            )
        } else if (ksValueParameter.isAnnotationPresent(Body::class)) {
            return RequestParam(
                ksValueParameter.name?.asString()!!,
                ksValueParameter.name?.asString()!!, RequestParamType.BODY
            )
        } else if (ksValueParameter.isAnnotationPresent(MultipartForm::class)) {
            return RequestParam(
                ksValueParameter.name?.asString()!!,
                ksValueParameter.name?.asString()!!, RequestParamType.MULTIPART_FORM
            )
        } else if (ksValueParameter.isAnnotationPresent(Channel::class)) {
            return RequestParam(
                ksValueParameter.name?.asString()!!,
                ksValueParameter.name?.asString()!!, RequestParamType.CHANNEL
            )
        }
        return RequestParam.empty
    }
}
