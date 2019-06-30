package com.badoo.reaktive.test.scheduler

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.update

class TestScheduler(
    private val isManualProcessing: Boolean = false
) : Scheduler {

    val timer = Timer()
    private val _executors: AtomicReference<List<Executor>> = AtomicReference(emptyList(), true)
    val executors: List<Executor> = _executors.value

    override fun newExecutor(): Scheduler.Executor =
        Executor(timer, isManualProcessing)
            .also { executor ->
                _executors.update { it + executor }
            }

    override fun destroy() {
        _executors
            .getAndUpdate { emptyList() }
            .forEach(Scheduler.Executor::dispose)
    }

    fun process() {
        _executors
            .value
            .forEach(Executor::process)
    }

    class Timer {
        private val timeMillis = AtomicLong()
        private val listeners: AtomicReference<Set<() -> Unit>> = AtomicReference(emptySet(), true)
        val millis: Long get() = timeMillis.value

        fun addOnChangeListener(listener: () -> Unit) {
            listeners.update { it + listener }
        }

        fun removeOnChangeListener(listener: () -> Unit) {
            listeners.update { it - listener }
        }

        fun advanceBy(millis: Long) {
            timeMillis.incrementAndGet(millis)
            listeners.value.forEach { it() }
        }
    }

    class Executor(
        private val timer: Timer,
        private val isManualProcessing: Boolean
    ) : Scheduler.Executor {

        private val tasks: AtomicReference<List<Task>> = AtomicReference(emptyList(), true)
        private val _isDisposed = AtomicBoolean()
        override val isDisposed: Boolean get() = _isDisposed.value
        private val timerListener = ::processIfNeeded

        init {
            timer.addOnChangeListener(timerListener)
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            addTask(delayMillis, null, task)
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            addTask(startDelayMillis, periodMillis, task)
        }

        override fun cancel() {
            tasks.value = emptyList()
        }

        override fun dispose() {
            _isDisposed.value = true
            timer.removeOnChangeListener(timerListener)
            cancel()
        }

        fun process() {
            val timeMillis = timer.millis

            tasks
                .getAndUpdate { it.removeExpiredTasks(timeMillis) }
                .asSequence()
                .filter { it.startMillis <= timeMillis }
                .forEach { it.task() }
        }

        private fun processIfNeeded() {
            if (!isManualProcessing) {
                process()
            }
        }

        private fun addTask(startDelayMillis: Long, periodMillis: Long?, task: () -> Unit) {
            tasks.update {
                it
                    .plus(
                        Task(
                            startMillis = timer.millis + startDelayMillis,
                            periodMillis = periodMillis,
                            task = task
                        )
                    )
                    .sorted()
            }

            processIfNeeded()
        }

        private companion object {
            private fun List<Task>.removeExpiredTasks(timeMillis: Long): List<Task> =
                mapNotNull {
                    when {
                        it.startMillis > timeMillis -> it
                        it.periodMillis != null -> it.copy(startMillis = timeMillis + it.periodMillis)
                        else -> null
                    }
                }
        }
    }

    private data class Task(
        val startMillis: Long,
        val periodMillis: Long?,
        val task: () -> Unit
    ) : Comparable<Task> {
        private val sequenceNumber = sequencer.incrementAndGet(1L)

        override fun compareTo(other: Task): Int =
            if (this === other) {
                0
            } else {
                startMillis
                    .compareTo(other.startMillis)
                    .takeUnless { it == 0 }
                    ?: sequenceNumber.compareTo(other.sequenceNumber)
            }

        private companion object {
            private val sequencer = AtomicLong()
        }
    }
}