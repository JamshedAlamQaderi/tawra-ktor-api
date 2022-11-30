package com.jamshedalamqaderi.tawraktorapi.visitors

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Delete
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Get
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Patch
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Post
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Put
import com.jamshedalamqaderi.tawraktorapi.exceptions.UnknownRequestMethodException
import com.jamshedalamqaderi.tawraktorapi.interfaces.ClientCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.RequestMethodVisitor
import com.jamshedalamqaderi.tawraktorapi.interfaces.RequestParamVisitor
import com.jamshedalamqaderi.tawraktorapi.models.RequestParam
import com.jamshedalamqaderi.tawraktorapi.models.RequestParamType
import com.jamshedalamqaderi.tawraktorapi.utils.ext.AnnotationExtensions.findAnnotatedWith
import com.jamshedalamqaderi.tawraktorapi.utils.ext.AnnotationExtensions.isAnnotationExists
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSValueParameterExtensions.constructorParams
import com.jamshedalamqaderi.tawraktorapi.utils.ext.qualifiedType
import com.soywiz.korio.file.VfsFile

typealias RequestBodyBlock = (List<RequestParam>) -> Unit

class ClientRequestMethodVisitorImpl(
    private val logger: KSPLogger,
    private val codeAppender: ClientCodeAppender,
    private val parentPath: String,
) : RequestMethodVisitor {
    private val requestParamVisitor: RequestParamVisitor<RequestParam> =
        ClientRequestParamVisitorImpl(logger, codeAppender)

    @OptIn(KspExperimental::class)
    override fun visit(ksFunctionDeclaration: KSFunctionDeclaration) {
        codeAppender.addImport("io.ktor.client.call", listOf("body"))
        createFunction(
            ksFunctionDeclaration.simpleName.asString(),
            ksFunctionDeclaration.returnType?.qualifiedType!!,
            paramBlock = {
                ksFunctionDeclaration
                    .parameters
                    .map(requestParamVisitor::visit)
            },
            bodyBlock = if (ksFunctionDeclaration.isAnnotationPresent(Get::class)) {
                generateRequestBody(
                    "get",
                    ksFunctionDeclaration.getAnnotationsByType(Get::class).first().path,
                    ksFunctionDeclaration
                )
            } else if (ksFunctionDeclaration.isAnnotationPresent(Post::class)) {
                generateRequestBody(
                    "post",
                    ksFunctionDeclaration.getAnnotationsByType(Post::class).first().path,
                    ksFunctionDeclaration
                )
            } else if (ksFunctionDeclaration.isAnnotationPresent(Put::class)) {
                generateRequestBody(
                    "put",
                    ksFunctionDeclaration.getAnnotationsByType(Put::class).first().path,
                    ksFunctionDeclaration
                )
            } else if (ksFunctionDeclaration.isAnnotationPresent(Patch::class)) {
                generateRequestBody(
                    "patch",
                    ksFunctionDeclaration.getAnnotationsByType(Patch::class).first().path,
                    ksFunctionDeclaration
                )
            } else if (ksFunctionDeclaration.isAnnotationPresent(Delete::class)) {
                generateRequestBody(
                    "delete",
                    ksFunctionDeclaration.getAnnotationsByType(Delete::class).first().path,
                    ksFunctionDeclaration
                )
            } else {
                throw UnknownRequestMethodException(
                    ksFunctionDeclaration
                        .annotations
                        .map { it.annotationType.qualifiedType }
                        .joinToString(", ")
                )
            }
        )
    }

    private fun generateRequestBody(
        requestName: String,
        path: String,
        ksFunctionDeclaration: KSFunctionDeclaration
    ): RequestBodyBlock {
        return { requestParams ->
            val urlPath = generatePath(parentPath + path, requestParams)
            codeAppender.addImport("io.ktor.client.request", listOf(requestName))
            codeAppender.addCode("\nreturn client.$requestName(%P){", urlPath)
            addBodyToRequest(requestParams)
            addFormParamsToRequest(requestName, requestParams)
            addMultipartFormToRequest(requestParams, ksFunctionDeclaration)
            codeAppender.addStatement("}.body()", urlPath)
        }
    }

    private fun addBodyToRequest(
        requestParams: List<RequestParam>
    ) {
        val body = requestParams.find { it.paramType == RequestParamType.BODY }
        if (body != null) {
            codeAppender.addImport("io.ktor.http", listOf("ContentType", "contentType"))
            codeAppender.addImport("io.ktor.client.request", listOf("setBody"))
            codeAppender.addCode("\ncontentType(ContentType.Application.Json)")
            codeAppender.addCode("\nsetBody(${body.paramName})")
        }
    }

    private fun addFormParamsToRequest(
        requestName: String,
        requestParams: List<RequestParam>,
    ) {
        val formParams = requestParams
            .filter { it.paramType == RequestParamType.FORM_PARAM }
            .joinToString("\n") { param -> "append(\"${param.paramKey}\", \"$${param.paramName}\")" }
        if (formParams.isEmpty()) return
        codeAppender.addImport("io.ktor.http", listOf("Parameters"))
        if (requestName == "get") {
            codeAppender.addStatement("url.parameters.appendAll(Parameters.build {")
            codeAppender.addStatement(formParams)
            codeAppender.addStatement("})")
        } else {
            codeAppender.addStatement("setBody(FormDataContent(Parameters.build {")
            codeAppender.addStatement(formParams)
            codeAppender.addStatement("}))")
        }
    }

    private fun addMultipartFormToRequest(
        requestParams: List<RequestParam>,
        ksFunctionDeclaration: KSFunctionDeclaration
    ) {
        if (ksFunctionDeclaration.parameters.isAnnotationExists<MultipartForm>()) {
            val multipartFormValue =
                ksFunctionDeclaration.parameters.findAnnotatedWith<MultipartForm>()
            val multipartFormParam =
                requestParams.find { it.paramType == RequestParamType.MULTIPART_FORM }
            if (multipartFormValue != null && multipartFormParam != null) {
                val typeParams = multipartFormValue.constructorParams()
                val multipartFormData = typeParams.joinToString("\n") { ksValueParameter ->
                    if (ksValueParameter.type.qualifiedType == VfsFile::class.qualifiedName) {
                        createMultipartVfsFileAppendCode(multipartFormParam, ksValueParameter)
                    } else {
                        """
                    append(
                        "${ksValueParameter.name?.asString()}",
                        "${'$'}{${multipartFormParam.paramName}.${ksValueParameter.name?.asString()}}"
                    )
                    """.trimIndent()
                    }
                }
                codeAppender.addImport("io.ktor.http", listOf("ContentType", "contentType"))
                codeAppender.addImport("io.ktor.client.request", listOf("setBody"))
                codeAppender.addImport(
                    "io.ktor.client.request.forms",
                    listOf("MultiPartFormDataContent", "formData")
                )
                codeAppender.addStatement("\nsetBody(MultiPartFormDataContent(formData {")
                codeAppender.addCode(multipartFormData)
                codeAppender.addStatement("}))")
            }
        }
    }

    private fun createMultipartVfsFileAppendCode(
        requestParam: RequestParam,
        ksValueParameter: KSValueParameter,
    ): String {
        codeAppender.addImport("com.soywiz.korio.file", listOf("baseName"))
        codeAppender.addImport("com.soywiz.korio.net", listOf("mimeType"))
        codeAppender.addImport("io.ktor.http", listOf("Headers", "HttpHeaders"))
        codeAppender.addImport("kotlinx.coroutines", listOf("runBlocking"))
        val referenceName = "${requestParam.paramName}.${ksValueParameter.name?.asString()}"
        return """
            append(
                "${ksValueParameter.name?.asString()}",
                runBlocking{ $referenceName.readBytes() },
                Headers.build{
                    append(HttpHeaders.ContentType, $referenceName.mimeType().mime)
                    append(HttpHeaders.ContentDisposition, "filename=\"${'$'}{$referenceName.baseName}\"")
                }
            )
        """.trimIndent()
    }

    private fun generatePath(actualPath: String, params: List<RequestParam>): String {
        var path = actualPath
        // inject variable to path params
        params
            .filter { it.paramType == RequestParamType.PATH_PARAM }
            .forEach { param ->
                path = path.replace("{${param.paramKey}}", "$" + param.paramName)
            }
        // inject query param values
        val queries = params
            .filter { it.paramType == RequestParamType.QUERY_PARAM }
            .joinToString("=") { param -> "${param.paramKey}=$${param.paramName}" }
        if (queries.isNotEmpty()) {
            path = "$path?$queries"
        }
        return path
    }

    private fun createFunction(
        functionName: String,
        qualifiedReturnType: String,
        paramBlock: () -> List<RequestParam>,
        bodyBlock: (List<RequestParam>) -> Unit
    ) {
        codeAppender.addCode("\nsuspend fun $functionName(")
        val requestParam = paramBlock()
        codeAppender.addCode("): $qualifiedReturnType {")
        bodyBlock(requestParam)
        codeAppender.addStatement("}")
    }
}