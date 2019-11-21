package com.badoo.reaktive.utils

internal actual val currentThreadId: Long get() = Thread.currentThread().id

internal actual val currentThreadName: String get() = Thread.currentThread().name
