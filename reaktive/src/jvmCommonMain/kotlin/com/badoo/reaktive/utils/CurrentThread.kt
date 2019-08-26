package com.badoo.reaktive.utils

internal actual val currentThreadId: Long get() = Thread.currentThread().id