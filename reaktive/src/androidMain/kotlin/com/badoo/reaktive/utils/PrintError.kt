package com.badoo.reaktive.utils

import android.util.Log

internal actual fun printError(error: Any?) {
    Log.e("Reaktive", error.toString())
}