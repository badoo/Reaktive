package com.badoo.reaktive.utils

import platform.Foundation.NSThread

// pthread_t.__sig returns same value for all threads, pthread_mach_thread_np() is not available in Kotlin/Native
internal actual val currentThreadId: Long get() = NSThread.currentThread.hashCode().toLong()

internal actual val currentThreadName: String get() = NSThread.currentThread.name ?: "thread_$currentThreadId"
