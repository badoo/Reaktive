package com.badoo.reaktive.looperthread

import com.badoo.reaktive.utils.DelayQueue
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

internal class LooperThread {

    private val queue = DelayQueue<Message>()
    private val worker = Worker.start(true)

    init {
        freeze()
        worker.execute(TransferMode.SAFE, { this }) { it.loop() }
    }

    fun schedule(token: Any, startTimeMillis: Long, task: () -> Unit) {
        queue.offerAt(Message(token, task), startTimeMillis)
    }

    fun cancel(token: Any) {
        queue.removeIf { it.token == token }
    }

    fun destroy() {
        worker.requestTermination(processScheduledJobs = false)
        queue.terminate()
    }

    private fun loop() {
        while (true) {
            val message = queue.take() ?: break
            message.task()
        }
        queue.destroy()
    }

    private class Message(
        val token: Any,
        val task: () -> Unit
    )
}