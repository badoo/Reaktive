package com.badoo.reaktive.utils.queue

interface Queue<T> : Iterable<T> {

    val peek: T?
    val size: Int
    val isEmpty: Boolean

    fun offer(item: T)

    fun poll(): T?

    fun clear()
}
