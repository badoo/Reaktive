@file:JvmName("PrintError")

package com.badoo.reaktive.utils

internal actual fun printError(error: Any?) {
    System.err.println(error)
}
