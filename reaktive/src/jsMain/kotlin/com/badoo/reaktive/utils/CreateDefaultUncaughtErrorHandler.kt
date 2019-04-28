package com.badoo.reaktive.utils

internal actual fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit =
    { e ->
        console.error(e)
    }