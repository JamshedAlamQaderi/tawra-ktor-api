package com.jamshedalamqaderi.tawraktorapi.api

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object RequestDataHandler {
    suspend inline fun handleMultipartData(
        outputDir: String,
        multipartData: MultiPartData
    ): HashMap<String, Any?> {
        val paramMap = hashMapOf<String, Any?>()
        multipartData.forEachPart { partData ->
            when (partData) {
                is PartData.FormItem -> {
                    paramMap[partData.name!!] = partData.value
                }

                is PartData.FileItem -> {
                    val fileName = partData.originalFileName as String
                    val inputFile = partData.streamProvider()
                    val saveFile = File("$outputDir/$fileName")
                    withContext(Dispatchers.IO) {
                        FileOutputStream(saveFile).use {
                            inputFile.copyTo(it)
                        }
                    }
                    paramMap[partData.name!!] = saveFile
                }

                else -> {}
            }
        }
        return paramMap
    }
}
