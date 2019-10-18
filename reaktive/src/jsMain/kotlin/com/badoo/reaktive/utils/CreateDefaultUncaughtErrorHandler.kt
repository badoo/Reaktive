package com.badoo.reaktive.utils

internal actual fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit = ::printError
