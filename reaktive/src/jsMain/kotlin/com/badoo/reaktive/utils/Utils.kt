package com.badoo.reaktive.utils

internal actual inline fun <T> synchronized(lock: Any, block: () -> T): T = block()
