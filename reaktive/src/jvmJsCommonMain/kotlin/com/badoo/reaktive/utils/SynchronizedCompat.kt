package com.badoo.reaktive.utils

internal expect inline fun <T> synchronizedCompat(lock: Any, block: () -> T): T
