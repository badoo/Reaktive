package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.serializer.serializer
import com.badoo.reaktive.utils.uptimeMillis

internal class TrampolineScheduler(
    private val getUptimeMillis: () -> Long = ::uptimeMillis,
    private val sleep: (mills: Long) -> Boolean
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor =
        ExecutorImpl(getUptimeMillis, sleep)
            .also(disposables::add)

    override fun destroy() {
        disposables.dispose()
    }

    private class ExecutorImpl(
        private val getUptimeMillis: () -> Long,
        private val sleep: (mills: Long) -> Boolean
    ) : Scheduler.Executor {

        private val serializer = serializer(comparator = Comparator(Task::compareTo), onValue = ::execute)
        private val _isDisposed = AtomicBoolean()
        override val isDisposed: Boolean get() = _isDisposed.value

        override fun dispose() {
            if (_isDisposed.compareAndSet(false, true)) {
                serializer.clear()
            }
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            submit(delayMillis, -1L, task)
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            submit(startDelayMillis, periodMillis, task)
        }

        override fun cancel() {
            serializer.clear()
        }

        private fun submit(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            submit(Task(getUptimeMillis() + startDelayMillis, periodMillis, task))
        }

        private fun submit(task: Task) {
            if (!isDisposed) {
                serializer.accept(task)
            }
        }

        private fun execute(task: Task): Boolean {
            if (isDisposed) {
                return false
            }

            val delay = task.startTime - getUptimeMillis()
            if ((delay > 0) && !sleep(delay)) {
                return false
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

        private data class Task(
            val startTime: Long,
            val periodMillis: Long,
            val task: () -> Unit
        ) : Comparable<Task> {
            private val sequenceNumber = sequencer.addAndGet(1)

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