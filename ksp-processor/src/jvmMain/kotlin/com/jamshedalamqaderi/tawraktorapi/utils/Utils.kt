package com.jamshedalamqaderi.tawraktorapi.utils

import com.jamshedalamqaderi.tawraktorapi.api.annotations.Body
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Channel
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Delete
import com.jamshedalamqaderi.tawraktorapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Get
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnConnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnDisconnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnError
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnMessage
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Patch
import com.jamshedalamqaderi.tawraktorapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Post
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Put
import com.jamshedalamqaderi.tawraktorapi.api.annotations.QueryParam

object Utils {
    val listOfRequestMethodAnnotations = listOf(
        Get::class,
        Post::class,
        Put::class,
        Patch::class,
        Delete::class
    )
    val listOfRequestParamAnnotations = listOf(
        PathParam::class,
        QueryParam::class,
        FormParam::class,
        Body::class,
        MultipartForm::class,
        Channel::class
    )

    val listOfWebsocketEventAnnotations = listOf(
        OnConnected::class,
        OnDisconnected::class,
        OnError::class,
        OnMessage::class,
    )
}