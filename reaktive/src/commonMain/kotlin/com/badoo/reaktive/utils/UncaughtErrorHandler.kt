package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicReference

@Suppress("ObjectPropertyName")
private val _reaktiveUncaughtErrorHandler: AtomicReference<(Throwable) -> Unit> =
    AtomicReference(createDefaultUncaughtErrorHandler())

var reaktiveUncaughtErrorHandler: (Throwable) -> Unit
    get() = _reaktiveUncaughtErrorHandler.value
    set(value) {
        _reaktiveUncaughtErrorHandler.value = value
    }

fun resetReaktiveUncaughtErrorHandler() {
    reaktiveUncaughtErrorHandler = createDefaultUncaughtErrorHandler()
}

internal expect fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit
