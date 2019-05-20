package com.badoo.reaktive.utils.atomicreference

inline fun <T> AtomicReference<T>.getAndUpdate(update: (T) -> T): T {
    var prev: T
    do {
        prev = value
    } while (!compareAndSet(prev, update(prev)))

    return prev
}

inline fun <T> AtomicReference<T>.updateAndGet(update: (T) -> T): T {
    var next: T
    do {
        val prev = value
        next = update(prev)
    } while (!compareAndSet(prev, next))

    return next
}

inline fun <T> AtomicReference<T>.update(update: (T) -> T) {
    getAndUpdate(update)
}