package com.jamshedalamqaderi.tawraktorapi.demo



import com.jamshedalamqaderi.tawraktorapi.tawraKtorApiRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            tawraKtorApiRoutes()
        }
    }.start(wait = true)
}