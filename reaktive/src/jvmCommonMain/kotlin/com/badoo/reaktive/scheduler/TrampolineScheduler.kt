package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.utils.PrioritySerializer
import java.util.concurrent.atomic.AtomicLong

internal class TrampolineScheduler(
    private val getUptimeMillis: () -> Long
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor =
        ExecutorImpl(getUptimeMillis)
            .also(disposables::add)

    override fun destroy() {
        disposables.dispose()
    }

    private class ExecutorImpl(
        private val getUptimeMillis: () -> Long
    ) : Scheduler.Executor {

        @Volatile
        private var serializer: PrioritySerializer<Task>? =
            object : PrioritySerializer<Task>() {
                override fun onValue(value: Task): Boolean = execute(value)
            }

        private val monitor = Any()

        override val isDisposed: Boolean get() = serializer == null

        override fun dispose() {
            if (serializer != null) {
                val serializerToClear: PrioritySerializer<*>
                synchronized(monitor) {
                    serializerToClear = serializer ?: return
                    serializer = null
                }
                serializerToClear.clear()
            }
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            submit(delayMillis, -1L, task)
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            submit(startDelayMillis, periodMillis, task)
        }

        override fun cancel() {
            executeIfNotDisposed(PrioritySerializer<*>::clear)
        }

        private fun submit(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            submit(Task(getUptimeMillis() + startDelayMillis, periodMillis, task))
        }

        private fun submit(task: Task) {
            executeIfNotDisposed { it.accept(task) }
        }

        private fun execute(task: Task): Boolean {
            if (isDisposed) {
                return false
            }

            val delay = task.startTime - getUptimeMillis()
            if (delay > 0) {
                try {
                    Thread.sleep(delay)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    return false
                }
            }

            if (isDisposed) {
                return false
            }

            val nextStartMillis = if (task.periodMillis >= 0L) getUptimeMillis() + task.periodMillis else -1L

            task.task()

            if (task.periodMillis >= 0L) {
                submit(task.copy(startTime = nextStartMillis))
            }

            return true
        }

        private inline fun <T> executeIfNotDisposed(block: (PrioritySerializer<Task>) -> T): T? {
            if (serializer != null) {
                synchronized(monitor) {
                    return serializer?.let(block)
                }
            }

            return null
        }

        private data class Task(
            val startTime: Long,
            val periodMillis: Long,
            val task: () -> Unit
        ) : Comparable<Task> {
            private val sequenceNumber = sequencer.getAndIncrement()

            override fun compareTo(other: Task): Int =
                if (this === other) {
                    0
                } else {
                    startTime
                        .compareTo(other.startTime)
                        .takeUnless { it == 0 }
                        ?: sequenceNumber.compareTo(other.sequenceNumber)
                }

            private companion object {
                private val sequencer = AtomicLong()
            }
        }
    }
}