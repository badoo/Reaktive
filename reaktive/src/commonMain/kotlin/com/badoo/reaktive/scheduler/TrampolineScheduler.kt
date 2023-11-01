package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.clock.Clock
import com.badoo.reaktive.utils.clock.DefaultClock
import com.badoo.reaktive.utils.coerceAtLeastZero
import com.badoo.reaktive.utils.serializer.serializer
import kotlin.time.Duration
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

internal class TrampolineScheduler(
    private val clock: Clock = DefaultClock,
    private val sleep: (Duration) -> Boolean
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = ExecutorImpl(disposables, clock, sleep)

    override fun destroy() {
        disposables.dispose()
    }

    private class ExecutorImpl(
        private val disposables: CompositeDisposable,
        private val clock: Clock,
        private val sleep: (Duration) -> Boolean
    ) : Scheduler.Executor {

        private val serializer = serializer(comparator = Comparator(Task::compareTo), onValue = ::execute)
        private val _isDisposed = AtomicBoolean()
        override val isDisposed: Boolean get() = _isDisposed.value

        init {
            disposables += this
        }

        override fun dispose() {
            if (_isDisposed.compareAndSet(false, true)) {
                serializer.clear()
                disposables -= this
            }
        }

        override fun submit(delay: Duration, period: Duration, task: () -> Unit) {
            submit(
                Task(
                    startTime = clock.uptime + delay.coerceAtLeastZero(),
                    period = period.coerceAtLeastZero(),
                    task = task,
                )
            )
        }

        override fun cancel() {
            serializer.clear()
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

            val delay = task.startTime - clock.uptime
            if (delay.isPositive() && !sleep(delay)) {
                return false
            }

            if (isDisposed) {
                return false
            }

            val nextStart = if (task.period.isInfinite()) null else clock.uptime + task.period

            task.task()

            if (nextStart != null) {
                submit(task.copy(startTime = nextStart))
            }

            return true
        }

        private data class Task(
            val startTime: ValueTimeMark,
            val period: Duration,
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
