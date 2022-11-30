package com.jamshedalamqaderi.tawraktorapi.rest

import com.soywiz.korio.`file`.baseName
import com.soywiz.korio.net.mimeType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking

class SimpleApiExample(private val client: HttpClient){
    companion object {
        private var instance: SimpleApiExample? = null

        fun getInstance(client: HttpClient): SimpleApiExample {
            if (instance == null) {
                instance = SimpleApiExample(client)
            }
            return instance!!
        }
    }
suspend fun findAllData(taskId : kotlin.Int?,name : kotlin.String?,model :
    com.jamshedalamqaderi.tawraktorapi.demo.MultipartModel,): kotlin.String {
return client.get("""/api/findAll/$taskId?name=$name"""){
    setBody(MultiPartFormDataContent(formData {
append(
    "username",
    "${model.username}"
)
append(
    "profilePicture",
    runBlocking{ model.profilePicture.readBytes() },
    Headers.build{
        append(HttpHeaders.ContentType, model.profilePicture.mimeType().mime)
        append(HttpHeaders.ContentDisposition, "filename=\"${model.profilePicture.baseName}\"")
    }
)}))
}.body()
}

}