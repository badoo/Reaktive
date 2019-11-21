package com.badoo.reaktive.utils

internal actual fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit = { e ->
    Thread
        .currentThread()
        .also { thread ->
            thread
                .uncaughtExceptionHandler
                .uncaughtException(thread, e)
        }
}
