package com.jamshedalamqaderi.tawraapi

import com.jamshedalamqaderi.tawraapi.rest.simpleApiExample
import io.ktor.server.routing.Route
import kotlin.Unit

public fun Route.tawraKtorApiRoutes(): Unit {
  simpleApiExample()
}
