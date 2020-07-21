package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

inline fun AtomicInt.updateAndGet(update: (Int) -> Int): Int {
    var next: Int
    do {
        val prev = value
        next = update(prev)
    } while (!compareAndSet(prev, next))

    return next
}

operator fun AtomicInt.getValue(thisRef: Any?, property: KProperty<*>): Int = value

operator fun AtomicInt.setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
    this.value = value
}
