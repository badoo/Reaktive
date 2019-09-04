package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicBoolean

@Suppress("ObjectPropertyName") // Backing field
private val _isPrintErrorEnabled = AtomicBoolean(true)

internal var isPrintErrorEnabled
    get() = _isPrintErrorEnabled.value
    set(value) {
        _isPrintErrorEnabled.value = value
    }

internal expect fun printError(error: Any?)