package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@Suppress("UNCHECKED_CAST") // https://youtrack.jetbrains.com/issue/KT-57412
@InternalReaktiveApi
inline fun <T> AtomicReference<T>.getAndChange(update: (T) -> T): T {
    while (true) {
        val prev: Any? = value
        val next: Any? = update(prev as T)

        if (compareAndSet(prev, next as T)) {
            return prev
        }
    }
}

@Suppress("UNCHECKED_CAST") // https://youtrack.jetbrains.com/issue/KT-57412
@InternalReaktiveApi
inline fun <T, R : T> AtomicReference<T>.changeAndGet(update: (T) -> R): R {
    while (true) {
        val prev: Any? = value
        val next: Any? = update(prev as T)

        if (compareAndSet(prev, next as R)) {
            return next
        }
    }
}

@InternalReaktiveApi
inline fun <T> AtomicReference<T>.change(update: (T) -> T) {
    getAndChange(update)
}
