package com.badoo.reaktive.utils

import java.util.concurrent.TimeUnit

internal actual val uptimeMillis: Long get() = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())