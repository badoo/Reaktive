package com.badoo.reaktive.utils.atomicreference

internal inline fun <T> AtomicReference<T>.getAndUpdate(update: (T) -> T): T {
    var var2: T
    do {
        var2 = value
    } while (!compareAndSet(var2, update(var2)))

    return var2
}

internal inline fun <T> AtomicReference<T>.updateAndGet(update: (T) -> T): T {
    var var3: T
    do {
        val var2 = value
        var3 = update(var2)
    } while (!compareAndSet(var2, var3))

    return var3
}

internal inline fun <T> AtomicReference<T>.update(update: (T) -> T) {
    getAndUpdate(update)
}