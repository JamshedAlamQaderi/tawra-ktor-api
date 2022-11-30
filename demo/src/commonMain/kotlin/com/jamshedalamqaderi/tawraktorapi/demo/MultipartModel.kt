package com.jamshedalamqaderi.tawraktorapi.demo

import com.soywiz.korio.file.VfsFile

data class MultipartModel(val username: String?, val profilePicture: VfsFile)