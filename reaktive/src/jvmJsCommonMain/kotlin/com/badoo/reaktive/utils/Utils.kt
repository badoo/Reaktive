package com.badoo.reaktive.utils

internal expect inline fun <T> synchronized(lock: Any, block: () -> T): T
