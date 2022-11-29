package com.jamshedalamqaderi.tawraapi.demo

import com.soywiz.korio.file.VfsFile

data class MultipartModel(val username: String?, val profilePicture: VfsFile)