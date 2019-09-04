package com.badoo.reaktive.utils

internal actual fun printError(error: Any?) {
    if (isPrintErrorEnabled) {
        console.error(error)
    }
}