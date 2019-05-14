package com.badoo.reaktive.testutils

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.getAndUpdate
import com.badoo.reaktive.utils.atomicreference.update

class TestScheduler : Scheduler {

    val timer = Timer()
    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor =
        ExecutorImpl(timer)
            .also(disposables::add)

    override fun destroy() {
        disposables.dispose()
    }

    class Timer {
        private val timeMillis = AtomicReference(0L)
        private val listeners: AtomicReference<Set<() -> Unit>> = AtomicReference(emptySet(), true)
        val millis: Long get() = timeMillis.value

        fun addOnChangeListener(listener: () -> Unit) {
            listeners.update { it + listener }
        }

        fun removeOnChangeListener(listener: () -> Unit) {
            listeners.update { it - listener }
        }

        fun advanceBy(millis: Long) {
            timeMillis.update { it + millis }
            listeners.value.forEach { it() }
        }
    }

    private class ExecutorImpl(
        private val timer: Timer
    ) : Scheduler.Executor {

        private val timerListener = ::process

        init {
            timer.addOnChangeListener(timerListener)
        }

        private val tasks: AtomicReference<List<Task>> = AtomicReference(emptyList(), true)

        override fun submit(delayMillis: Long, task: () -> Unit) {
            addTask(delayMillis, null, task)
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            addTask(startDelayMillis, periodMillis, task)
        }

        override fun cancel() {
            tasks.value = emptyList()
        }

        private val _isDisposed = AtomicReference(false)
        override val isDisposed: Boolean get() = _isDisposed.value

        override fun dispose() {
            _isDisposed.value = true
            timer.removeOnChangeListener(timerListener)
            cancel()
        }

        fun process() {
            val timeMillis = timer.millis

            val oldTasks =
                tasks.getAndUpdate { list ->
                    arrayListOf<Task>()
                        .apply {
                            list.forEach { task ->
                                if (task.startMillis > timeMillis) {
                                    add(task)
                                } else if (task.periodMillis != null) {
                                    add(task.copy(startMillis = timeMillis + task.periodMillis))
                                }
                            }
                        }
                }

            oldTasks
                .asSequence()
                .filter { it.startMillis <= timeMillis }
                .forEach { it.task() }
        }

        private fun addTask(startDelayMillis: Long, periodMillis: Long?, task: () -> Unit) {
            tasks.update {
                it.plus(
                    Task(
                        startMillis = timer.millis + startDelayMillis,
                        periodMillis = periodMillis,
                        task = task
                    )
                )
            }

            process()
        }
    }

    private data class Task(
        val startMillis: Long,
        val periodMillis: Long?,
        val task: () -> Unit
    )
}