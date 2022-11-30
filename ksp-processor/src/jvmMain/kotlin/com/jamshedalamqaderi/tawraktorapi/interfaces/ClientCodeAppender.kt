package com.jamshedalamqaderi.tawraktorapi.interfaces

interface ClientCodeAppender : CodeAppender {
    fun addExtensionProperty(propertyName: String, qualifiedReturnType: String)
    fun createClass(name: String, block: () -> Unit)
}