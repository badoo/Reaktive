package com.badoo.reaktive.utils

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

/*
 * Not cancellable nor destroyable, implement when needed. Currently used only as singleton.
 * DelayQueue should be destroyed, but all readers and writers must be cancelled first.
 * See LooperThread for sample implementation.
 */
internal class ExpirationPool<T : Any>(
    private val onItemExpired: (T) -> Unit
) {

    private val queue = DelayQueue<T>()

    init {
        freeze()
        Worker.start(true).execute(TransferMode.SAFE, { this }) { it.drainQueue() }
    }

    fun acquire(): T? = queue.removeFirst()

    fun release(item: T, timeoutMillis: Long) {
        queue.offer(item, timeoutMillis)
    }

    private fun drainQueue() {
        while (true) {
            onItemExpired(queue.take() ?: break)
        }
        queue.destroy()
    }
}
