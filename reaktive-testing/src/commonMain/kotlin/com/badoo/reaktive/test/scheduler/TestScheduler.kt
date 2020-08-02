package com.badoo.reaktive.test.scheduler

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.Scheduler.Executor
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.freeze

class TestScheduler(
    isManualProcessing: Boolean = false
) : Scheduler {

    private val _isManualProcessing = AtomicBoolean(isManualProcessing)
    var isManualProcessing: Boolean
        get() = _isManualProcessing.value
        set(value) {
            _isManualProcessing.value = value
            if (!value) {
                process()
            }
        }

    private val _timer = TimerImpl()
    val timer: Timer = _timer
    private val _executors = AtomicReference<List<Executor>>(emptyList())
    val executors: List<Executor> get() = _executors.value
    private val tasks = AtomicReference<List<Task>>(emptyList())
    private var isProcessing = AtomicBoolean()

    init {
        freeze()
    }

    override fun newExecutor(): Executor {
        val executor = ExecutorImpl()
        _executors.update { it + executor }

        return executor
    }

    override fun destroy() {
        _executors
            .getAndUpdate { emptyList() }
            .forEach(Executor::dispose)
    }

    fun process() {
        if (isProcessing.compareAndSet(false, true)) {
            try {
                processActual()
            } finally {
                isProcessing.value = false
            }
        }
    }

    private fun processActual() {
        while (true) {
            val task = tasks.value.firstOrNull()?.takeIf { it.startMillis <= _timer.targetMillis } ?: break
            updateTasks {
                removeAt(0)
                if (task.periodMillis >= 0L) {
                    add(task.copy(startMillis = task.startMillis + task.periodMillis))
                    sort()
                }
            }

            _timer.millis = task.startMillis

            if (!task.executor.isDisposed) {
                task.task()
            }
        }

        _timer.millis = _timer.targetMillis
    }

    private fun processIfNeeded() {
        if (!_isManualProcessing.value) {
            process()
        }
    }

    private inline fun updateTasks(block: MutableList<Task>.() -> Unit) {
        tasks.update {
            it.toMutableList().also(block)
        }
    }

    interface Timer {
        val millis: Long

        fun advanceBy(millis: Long)
    }

    private inner class TimerImpl : Timer {
        private val _millis = AtomicLong()
        override var millis: Long
            get() = _millis.value
            set(value) {
                _millis.value = value
            }

        private val _requestedMillis = AtomicLong()
        val targetMillis: Long get() = _requestedMillis.value

        override fun advanceBy(millis: Long) {
            require(millis >= 0L) { "Millis must not be negative" }

            _requestedMillis.addAndGet(millis)
            processIfNeeded()
        }
    }

    private inner class ExecutorImpl : Executor {
        private val _isDisposed = AtomicBoolean()
        override val isDisposed: Boolean get() = _isDisposed.value

        override fun submit(delayMillis: Long, task: () -> Unit) {
            addTask(startDelayMillis = delayMillis, task = task)
            processIfNeeded()
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            addTask(startDelayMillis = startDelayMillis, periodMillis = periodMillis, task = task)
            processIfNeeded()
        }

        private fun addTask(startDelayMillis: Long, periodMillis: Long = -1L, task: () -> Unit) {
            updateTasks {
                add(
                    Task(
                        startMillis = timer.millis + startDelayMillis,
                        periodMillis = periodMillis,
                        executor = this@ExecutorImpl,
                        task = task
                    )
                )
                sort()
            }
        }

        override fun cancel() {
            updateTasks {
                removeAll { it.executor === this@ExecutorImpl }
            }
        }

        override fun dispose() {
            _isDisposed.value = true
            cancel()
        }
    }

    private data class Task(
        val startMillis: Long,
        val periodMillis: Long,
        val executor: Executor,
        val task: () -> Unit
    ) : Comparable<Task> {
        private val sequenceNumber = sequencer.addAndGet(1L)

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
