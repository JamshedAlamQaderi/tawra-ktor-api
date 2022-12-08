package com.jamshedalamqaderi.tawraktorapi.demo

import com.jamshedalamqaderi.tawraktorapi.api.annotations.Body
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Get
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnConnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnDisconnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnError
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnMessage
import com.jamshedalamqaderi.tawraktorapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.QueryParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Rest
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Websocket
import io.ktor.server.websocket.WebSocketServerSession

@Rest("/api")
class SimpleApiExample {

    @Get("/findAll/{task_id}")
    fun findAllData(
        @PathParam("task_id") taskId: Int?,
        @QueryParam("name") name: String?,
        @MultipartForm model: MultipartModel,
    ): String {
        return "found all $taskId --> $name"
    }
}

@Websocket("ws/{deviceId}")
class EventListener {

    @OnConnected
    fun onConnected(
        session: WebSocketServerSession,
        @PathParam deviceId: String,
        @QueryParam age: String?
    ) {
    }

    @OnMessage
    fun onMessage(@PathParam deviceId: String, @Body student: Student): Student {
        return Student.Empty
    }

    @OnError
    fun onError(t: Throwable) {
    }

    @OnDisconnected
    fun onDisconnected(session: WebSocketServerSession) {
    }
}
