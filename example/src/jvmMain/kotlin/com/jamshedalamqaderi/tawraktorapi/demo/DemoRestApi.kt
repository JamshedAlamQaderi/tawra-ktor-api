package com.jamshedalamqaderi.tawraktorapi.demo


import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import java.time.Duration
import java.util.Collections
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main(): Unit = runBlocking {
    val connections = Collections.synchronizedSet<DefaultWebSocketServerSession>(LinkedHashSet())
    embeddedServer(Netty, port = 8080) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingPeriod = Duration.ofSeconds(1)
            timeout = Duration.ofSeconds(1)
            maxFrameSize = Long.MAX_VALUE
        }
        install(ContentNegotiation) {
            json()
        }
        routing {
//            tawraKtorApiRoutes()
            webSocket("/echo") {
                println("Connected")
                try {
                    println(receiveDeserialized<Student>())
                    repeat(1000) {
                        delay(1000)
                        send(Frame.Text("Hello, world"))
                    }
                }finally {
                    println("Closed")
                }
            }
        }
    }.start(wait = true)
}