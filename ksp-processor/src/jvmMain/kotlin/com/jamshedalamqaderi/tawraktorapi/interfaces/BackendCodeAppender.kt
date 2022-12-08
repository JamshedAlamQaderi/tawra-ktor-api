package com.jamshedalamqaderi.tawraktorapi.interfaces

interface BackendCodeAppender : CodeAppender {
    fun addRoute(path: String, block: () -> Unit)
    fun addGet(path: String, block: () -> Unit)
    fun addPost(path: String, block: () -> Unit)
    fun addPut(path: String, block: () -> Unit)
    fun addPatch(path: String, block: () -> Unit)
    fun addDelete(path: String, block: () -> Unit)

    fun addWebSocket(path: String, block: () -> Unit)
}
