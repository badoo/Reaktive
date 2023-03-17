package com.badoo.reaktive.utils

import kotlinx.cinterop.UnsafeNumber
import platform.posix.pthread_self

@OptIn(UnsafeNumber::class)
internal actual val currentThreadId: Long
    get() = pthread_self().toLong()

// No way to get thread name in K/N Linux
internal actual val currentThreadName: String get() = "thread_$currentThreadId"
