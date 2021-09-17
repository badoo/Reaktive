package com.badoo.reaktive.utils.lock

import platform.posix.timespec
import platform.posix.timeval

// Workaround for KT-41509
internal expect fun timespec.set(t: timeval)
