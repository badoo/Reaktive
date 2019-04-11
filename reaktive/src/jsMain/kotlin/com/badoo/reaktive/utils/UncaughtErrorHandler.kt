package com.badoo.reaktive.utils

actual var reaktiveUncaughtErrorHandler: (Throwable) -> Unit =
    { e -> console.error(e) }
