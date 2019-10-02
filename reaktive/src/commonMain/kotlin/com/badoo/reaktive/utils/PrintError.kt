package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.native.concurrent.SharedImmutable

@Suppress("ObjectPropertyName") // Backing field
@SharedImmutable
private val _isPrintErrorEnabled = AtomicBoolean(true)

internal var isPrintErrorEnabled
    get() = _isPrintErrorEnabled.value
    set(value) {
        _isPrintErrorEnabled.value = value
    }

internal expect fun printError(error: Any?)