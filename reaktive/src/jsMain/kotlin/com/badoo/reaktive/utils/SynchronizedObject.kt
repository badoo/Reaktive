package com.badoo.reaktive.utils

@Suppress("EmptyDefaultConstructor")
internal actual open class SynchronizedObject actual constructor() {

    actual inline fun <T> synchronized(block: () -> T): T =
        block()
}
