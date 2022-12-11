package com.jamshedalamqaderi.tawraktorapi

import org.gradle.api.provider.Property

interface TawraKtorApiExtension {
    val packageName: Property<String>
}