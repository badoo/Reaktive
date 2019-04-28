package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomicreference.AtomicReference
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
@Suppress("ObjectPropertyName")
private val _reaktiveUncaughtErrorHandler: AtomicReference<(Throwable) -> Unit> =
    AtomicReference(createDefaultUncaughtErrorHandler(), true)

var reaktiveUncaughtErrorHandler: (Throwable) -> Unit
    get() = _reaktiveUncaughtErrorHandler.value
    set(value) {
        _reaktiveUncaughtErrorHandler.value = value
    }

internal expect fun createDefaultUncaughtErrorHandler(): (Throwable) -> Unit
