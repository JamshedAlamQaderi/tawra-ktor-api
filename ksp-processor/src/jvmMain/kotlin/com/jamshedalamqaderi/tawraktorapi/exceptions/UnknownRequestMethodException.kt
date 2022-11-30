package com.jamshedalamqaderi.tawraktorapi.exceptions

data class UnknownRequestMethodException(val name: String) :
    Exception("Unknown request method detected with name: $name")