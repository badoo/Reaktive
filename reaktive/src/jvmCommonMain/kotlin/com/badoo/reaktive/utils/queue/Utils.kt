package com.badoo.reaktive.utils.queue

internal fun <T> java.util.Queue<T>.toReaktive(): Queue<T> =
    object : Queue<T> {
        override val peek: T? get() = this@toReaktive.peek()
        override val size: Int get() = this@toReaktive.size

        override fun offer(item: T) {
            this@toReaktive.offer(item)
        }

        override fun poll(): T? = this@toReaktive.poll()

        override fun clear() {
            this@toReaktive.clear()
        }
    }