package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized

@Suppress("EmptyDefaultConstructor")
internal actual open class SynchronizedObject actual constructor() {

    private val lock = Lock()

    actual inline fun <T> synchronized(block: () -> T): T =
        lock.synchronized(block)
}
