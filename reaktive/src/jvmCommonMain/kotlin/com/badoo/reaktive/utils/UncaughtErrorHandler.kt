package com.badoo.reaktive.utils

actual var reaktiveUncaughtErrorHandler: (Throwable) -> Unit =
    { e ->
        Thread
            .currentThread()
            .also { thread ->
                thread
                    .uncaughtExceptionHandler
                    .uncaughtException(thread, e)
            }
    }