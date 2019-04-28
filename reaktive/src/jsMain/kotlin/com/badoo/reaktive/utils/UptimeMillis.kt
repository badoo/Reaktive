package com.badoo.reaktive.utils

import kotlin.browser.window

internal actual val uptimeMillis: Long
    get() = window.performance.now().toLong()
