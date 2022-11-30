package com.jamshedalamqaderi.tawraktorapi.utils

import com.jamshedalamqaderi.tawraktorapi.api.annotations.Body
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Channel
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Delete
import com.jamshedalamqaderi.tawraktorapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Get
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
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
}