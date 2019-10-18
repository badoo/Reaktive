package com.badoo.reaktive.utils

import platform.posix.pthread_self

internal actual val currentThreadId: Long get() = pthread_self().toLong()

// No way to get thread name in K/N Linux
internal actual val currentThreadName: String get() = "thread_$currentThreadId"
