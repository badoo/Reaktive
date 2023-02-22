package com.badoo.reaktive.utils

internal actual inline fun <T> synchronizedCompat(lock: Any, block: () -> T): T = block()
