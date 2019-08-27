package com.badoo.reaktive.utils

import kotlinx.cinterop.convert
import platform.posix.pthread_self

internal actual val currentThreadId: Long get() = pthread_self().convert()