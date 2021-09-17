package com.badoo.reaktive.utils

import platform.posix.pthread_self

// Moved from linuxCommonMain due to KT-41509
internal actual val currentThreadId: Long get() = pthread_self().toLong()
