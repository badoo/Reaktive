package com.badoo.reaktive.utils

// No way to get thread name in K/N Linux
internal actual val currentThreadName: String get() = "thread_$currentThreadId"
