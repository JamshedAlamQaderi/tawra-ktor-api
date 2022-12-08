package com.jamshedalamqaderi.tawraktorapi.demo

import kotlinx.serialization.Serializable

@Serializable
data class Student(val name: String) {
    companion object {
        val Empty = Student("")
    }
}
