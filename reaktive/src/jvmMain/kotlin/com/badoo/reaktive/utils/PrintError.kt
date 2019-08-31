@file:JvmName("PrintError")

package com.badoo.reaktive.utils

internal actual fun printError(error: Any?) {
    if (isPrintErrorEnabled) {
        System.err.println(error)
    }
}