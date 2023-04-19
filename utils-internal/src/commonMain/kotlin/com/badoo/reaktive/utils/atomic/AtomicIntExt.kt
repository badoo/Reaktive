package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.reflect.KProperty

@InternalReaktiveApi
inline fun AtomicInt.updateAndGet(update: (Int) -> Int): Int {
    var next: Int
    do {
        val prev = value
        next = update(prev)
    } while (!compareAndSet(prev, next))

    return next
}

@InternalReaktiveApi
operator fun AtomicInt.getValue(thisRef: Any?, property: KProperty<*>): Int = value

@InternalReaktiveApi
operator fun AtomicInt.setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
    this.value = value
}
