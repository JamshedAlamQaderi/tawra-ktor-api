package com.jamshedalamqaderi.tawraapi.exceptions

data class UnknownRequestMethodException(val name: String) :
    Exception("Unknown request method detected with name: $name")