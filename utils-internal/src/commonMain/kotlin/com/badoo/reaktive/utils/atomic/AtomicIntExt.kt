package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
inline fun AtomicInt.changeAndGet(update: (Int) -> Int): Int {
    var next: Int
    do {
        val prev = value
        next = update(prev)
    } while (!compareAndSet(prev, next))

    return next
}
