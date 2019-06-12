package com.badoo.reaktive.utils

import kotlin.js.Date

internal actual val uptimeMillis: Long
    get() = Date.now().toLong()
