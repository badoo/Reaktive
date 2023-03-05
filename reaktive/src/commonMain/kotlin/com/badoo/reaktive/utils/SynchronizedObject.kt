package com.badoo.reaktive.utils

@Suppress("EmptyDefaultConstructor")
internal expect open class SynchronizedObject() {

    inline fun <T> synchronized(block: () -> T): T
}
