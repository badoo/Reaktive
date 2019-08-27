package com.badoo.reaktive.utils

import platform.Foundation.NSThread

internal actual val currentThreadId: Long
    get() = pthread_self()?.pointed?.__sig?.toLong() ?: currentThreadName.hashCode().toLong()

internal actual val currentThreadName: String get() = NSThread.currentThread.name