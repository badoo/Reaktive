package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
inline fun <T> AtomicReference<T>.getAndChange(update: (T) -> T): T {
    var prev: T
    do {
        prev = value
    } while (!compareAndSet(prev, update(prev)))

    return prev
}

@InternalReaktiveApi
inline fun <T, R : T> AtomicReference<T>.changeAndGet(update: (T) -> R): R {
    var next: R
    do {
        val prev = value
        next = update(prev)
    } while (!compareAndSet(prev, next))

    return next
}

@InternalReaktiveApi
inline fun <T> AtomicReference<T>.change(update: (T) -> T) {
    getAndChange(update)
}
