package com.jamshedalamqaderi.tawraktorapi

import com.jamshedalamqaderi.tawraktorapi.rest.simpleApiExample
import io.ktor.server.routing.Route
import kotlin.Unit

public fun Route.tawraKtorApiRoutes(): Unit {
  simpleApiExample()
}
