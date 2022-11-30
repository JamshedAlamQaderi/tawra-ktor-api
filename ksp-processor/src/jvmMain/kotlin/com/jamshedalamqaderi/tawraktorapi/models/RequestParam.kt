package com.jamshedalamqaderi.tawraktorapi.models

data class RequestParam(
    val paramKey: String,
    val paramName: String,
    val paramType: RequestParamType
) {
    companion object {
        val empty = RequestParam("", "", RequestParamType.NONE)
    }
}

enum class RequestParamType {
    NONE,
    PATH_PARAM,
    QUERY_PARAM,
    FORM_PARAM,
    BODY,
    MULTIPART_FORM,
    CHANNEL
}
