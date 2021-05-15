package com.badoo.reaktive.utils

actual fun Throwable.printStack() {
    console.log(toString())
}
