package com.jamshedalamqaderi.tawraapi.utils

import com.jamshedalamqaderi.tawraapi.api.annotations.Body
import com.jamshedalamqaderi.tawraapi.api.annotations.Channel
import com.jamshedalamqaderi.tawraapi.api.annotations.Delete
import com.jamshedalamqaderi.tawraapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraapi.api.annotations.Get
import com.jamshedalamqaderi.tawraapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraapi.api.annotations.Patch
import com.jamshedalamqaderi.tawraapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraapi.api.annotations.Post
import com.jamshedalamqaderi.tawraapi.api.annotations.Put
import com.jamshedalamqaderi.tawraapi.api.annotations.QueryParam

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