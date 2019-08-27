package com.badoo.reaktive.utils

import platform.Foundation.NSThread
import platform.posix.pthread_self

internal actual val currentThreadId: Long
    get() = pthread_self()?.pointed?.__sig?.toLong() ?: NSThread.currentThread.name?.hashCode()?.toLong() ?: 0L

internal actual val currentThreadName: String
    get() = NSThread.currentThread.name ?: pthread_self()?.pointed?.__sig?.toString() ?: "unnamed"