package com.badoo.reaktive.utils

import java.util.concurrent.TimeUnit

internal class ExpirationPool<T : Any>(onItemExpired: (T) -> Unit) {

    private val queue = EnhancedDelayQueue<T>()
    private val daemon = Daemon(queue, onItemExpired)

    init {
        daemon.start()
    }

    fun acquire(): T? = queue.removeFirst()

    fun release(item: T, timeoutMillis: Long) {
        queue.offer(item, timeoutMillis, TimeUnit.MILLISECONDS)
    }

    fun destroy() {
        daemon.interrupt()
    }

    private class Daemon<T : Any>(
        private val queue: EnhancedDelayQueue<T>,
        private val onItemExpired: (T) -> Unit
    ) : Thread() {
        init {
            isDaemon = true
        }

        override fun run() {
            super.run()

            while (!isInterrupted) {
                try {
                    queue
                        .take()
                        .also(onItemExpired)
                } catch (e: InterruptedException) {
                    interrupt()
                    break
                }
            }
        }
    }
}