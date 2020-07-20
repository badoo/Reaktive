package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

fun <T> AtomicReference<T>.getAndSet(value: T): T = getAndUpdate { value }

inline fun <T> AtomicReference<T>.getAndUpdate(update: (T) -> T): T {
    var prev: T
    do {
        prev = value
    } while (!compareAndSet(prev, update(prev)))

    return prev
}

inline fun <T, R : T> AtomicReference<T>.updateAndGet(update: (T) -> R): R {
    var next: R
    do {
        val prev = value
        next = update(prev)
    } while (!compareAndSet(prev, next))

    return next
}

inline fun <T> AtomicReference<T>.update(update: (T) -> T) {
    getAndUpdate(update)
}

operator fun <T> AtomicReference<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

operator fun <T> AtomicReference<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}
