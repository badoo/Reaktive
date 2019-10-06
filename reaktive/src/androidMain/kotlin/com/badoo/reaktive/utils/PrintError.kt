@file:JvmName("PrintError")

package com.badoo.reaktive.utils

import android.util.Log

internal actual fun printError(error: Any?) {
    try {
        Log.e("Reaktive", error.toString())
    } catch (ignored: RuntimeException) {
        // Fails in unit tests
    }
}