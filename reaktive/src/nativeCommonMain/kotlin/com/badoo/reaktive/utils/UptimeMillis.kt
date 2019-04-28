package com.badoo.reaktive.utils

import kotlin.system.getTimeMillis

internal actual val uptimeMillis: Long get() = getTimeMillis()