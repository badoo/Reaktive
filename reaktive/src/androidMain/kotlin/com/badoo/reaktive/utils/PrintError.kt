@file:JvmName("PrintError")

package com.badoo.reaktive.utils

import android.util.Log

internal actual fun printError(error: Any?) {
    if (isPrintErrorEnabled) {
        Log.e("Reaktive", error.toString())
    }
}