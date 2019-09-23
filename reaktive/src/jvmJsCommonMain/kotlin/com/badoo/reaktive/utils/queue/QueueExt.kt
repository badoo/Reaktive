package com.badoo.reaktive.utils.queue

internal val Queue<*>.isEmpty: Boolean get() = size == 0

internal val Queue<*>.isNotEmpty: Boolean get() = size > 0
