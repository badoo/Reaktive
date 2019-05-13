package com.badoo.reaktive.looperthread

import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

internal class LooperThread {

    private val queue = MessageQueue().freeze()
    private val worker = Worker.start(true)
    private val isDestroyed = AtomicInt(0)

    init {
        worker.execute(TransferMode.SAFE, { ::loop.freeze() }) {
            it()
        }
    }

    fun schedule(token: Any, startTimeNanos: Long, task: () -> Unit) {
        queue.offer(token, startTimeNanos, task)
    }

    fun cancel(token: Any) {
        queue.clear(token)
    }

    fun destroy() {
        isDestroyed.value = 1
        queue.clear()
        worker.requestTermination(processScheduledJobs = false)
    }

    private fun loop() {
        while (isDestroyed.value == 0) {
            queue.take().invoke()
        }
    }
}