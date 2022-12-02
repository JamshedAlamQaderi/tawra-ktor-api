package com.jamshedalamqaderi.tawraktorapi.demo

import com.jamshedalamqaderi.tawraktorapi.api.annotations.Body
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Delete
import com.jamshedalamqaderi.tawraktorapi.api.annotations.FormParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Get
import com.jamshedalamqaderi.tawraktorapi.api.annotations.MultipartForm
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Patch
import com.jamshedalamqaderi.tawraktorapi.api.annotations.PathParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Post
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Put
import com.jamshedalamqaderi.tawraktorapi.api.annotations.QueryParam
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Rest
import java.io.File

@Rest("/api")
interface SimpleApiExampleV2 {

    @Get("/findAll")
    fun findAll(): String

    @Get("/findById/{id}")
    fun findById(@PathParam("id") id: Long): String


    @Get("/search")
    fun search(@QueryParam("q") q: String): String

    @Delete("{id}")
    fun deleteById(@PathParam id: String): Boolean

    @Post
    fun create(@Body student: Student): Boolean

    @Put
    fun update(@Body student: Student): Boolean

    @Patch
    fun updatePartially(@Body student: Student): Boolean

    @Post
    fun formParams(@FormParam("group") group: String, @FormParam age: String?): Boolean

    @Post
    fun multipartData(@MultipartForm multipartModel: MultipartModel): Boolean
}

@Rest("/api")
class SimpleApiExample {

    @Get("/findAll/{task_id}")
    fun findAllData(
        @PathParam("task_id") taskId: Int?,
        @QueryParam("name") name: String?,
        @MultipartForm model: MultipartModel,
    ): String {
        return "found all $taskId --> $name"
    }
}
