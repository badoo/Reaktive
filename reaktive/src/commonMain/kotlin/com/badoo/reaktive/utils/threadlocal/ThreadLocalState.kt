package com.badoo.reaktive.utils.threadlocal

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
internal object ThreadLocalState {

    private val map: MutableMap<Any, Any> = HashMap()
    private var currentKey = 0

    fun allocateKey(): Any = currentKey++

    operator fun get(key: Any): Any? = map[key]

    operator fun set(key: Any, value: Any?) {
        if (value == null) {
            map -= key
        } else {
            map[key] = value
        }
    }
}