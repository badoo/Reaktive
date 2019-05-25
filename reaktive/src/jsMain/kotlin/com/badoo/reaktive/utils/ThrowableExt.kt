package com.badoo.reaktive.utils

actual fun Throwable.printStack() {
    println(stackTrace)
    var inner: Throwable? = cause
    while (inner != null) {
        println("Caused by: ${inner.stackTrace}")
        inner = inner.cause
    }
}

private val Throwable.stackTrace: String
    get() =
        try {
            asDynamic().stack.toString()
        } catch (ignored: Throwable) {
            toString()
        }