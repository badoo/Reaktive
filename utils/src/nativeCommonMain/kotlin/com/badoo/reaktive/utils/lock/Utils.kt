package com.badoo.reaktive.utils.lock

import platform.posix.timespec

// Workaround for KT-41509
internal expect operator fun timespec.plusAssign(nanos: Long)
