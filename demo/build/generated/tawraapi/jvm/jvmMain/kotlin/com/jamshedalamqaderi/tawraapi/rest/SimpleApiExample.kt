package com.jamshedalamqaderi.tawraapi.rest

import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.`get`
import io.ktor.server.routing.route
import kotlin.Unit

public fun Route.simpleApiExample(): Unit {
  route("""/api"""){
  	val simpleApiExample = com.jamshedalamqaderi.demo.SimpleApiExample()
  get("""/findAll/{task_id}"""){

      val multipartData = call.receiveMultipart()

      val multipartMap =
          com.jamshedalamqaderi.tawraapi.api.RequestDataHandler.handleMultipartData("""upload/""",
          multipartData)
  call.respond(
  simpleApiExample.findAllData(
  call.parameters["""task_id"""]?.toIntOrNull(),
  call.request.queryParameters["""name"""],
  com.jamshedalamqaderi.tawraapi.demo.MultipartModel(
  multipartMap["""username"""] as kotlin.String?,
  multipartMap["""profilePicture"""] as com.soywiz.korio.file.VfsFile,
  )
  )
  )
  }
  }
}
