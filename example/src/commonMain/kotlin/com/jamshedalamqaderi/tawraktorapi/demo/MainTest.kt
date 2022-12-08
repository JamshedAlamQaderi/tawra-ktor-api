package com.jamshedalamqaderi.tawraktorapi.demo

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import kotlinx.coroutines.runBlocking


fun setup() = runBlocking {
    val client = HttpClient(CIO) {
        install(WebSockets) {

        }
        defaultRequest {

        }
    }
    val session: DefaultClientWebSocketSession = client.webSocketSession("/hello")
    while (true){
        val message = session.receiveDeserialized<Student>()
        session.sendSerialized(Student("name"))
    }
}
