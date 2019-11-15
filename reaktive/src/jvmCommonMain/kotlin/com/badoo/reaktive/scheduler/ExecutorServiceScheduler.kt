package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.handleReaktiveError
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

internal class ExecutorServiceScheduler(
    private val executorServiceStrategy: ExecutorServiceStrategy
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = ExecutorImpl(disposables, executorServiceStrategy)

    override fun destroy() {
        disposables.dispose()
        executorServiceStrategy.destroy()
    }

    private class ExecutorImpl(
        private val disposables: CompositeDisposable,
        private val executorServiceStrategy: ExecutorServiceStrategy
    ) : Scheduler.Executor {

        @Volatile
        private var executor: ScheduledExecutorService? = executorServiceStrategy.get()

        private val taskDisposables = CompositeDisposable()
        private val monitor: Any = taskDisposables
        override val isDisposed: Boolean get() = executor == null

        init {
            disposables += this
        }

        override fun dispose() {
            if (executor != null) {
                val executorToRecycle: ScheduledExecutorService
                synchronized(monitor) {
                    executorToRecycle = executor ?: return
                    executor = null
                }
                taskDisposables.dispose()
                executorServiceStrategy.recycle(executorToRecycle)
                disposables -= this
            }
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            executeIfNotRecycled {
                it.schedule(wrapSchedulerTaskSafe(task), delayMillis, TimeUnit.MILLISECONDS)
            }
                ?.toDisposable()
                ?.let(taskDisposables::add)
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            executeIfNotRecycled {
                it.scheduleAtFixedRate(
                    wrapSchedulerTaskSafe(task),
                    startDelayMillis,
                    periodMillis,
                    TimeUnit.MILLISECONDS
                )
            }
                ?.toDisposable()
                ?.let(taskDisposables::add)
        }

        override fun cancel() {
            taskDisposables.clear(true)
        }

        private inline fun <T> executeIfNotRecycled(block: (ScheduledExecutorService) -> T): T? {
            if (executor != null) {
                synchronized(monitor) {
                    return executor?.let(block)
                }
            }

            return null
        }

        private companion object {
            private fun Future<*>.toDisposable(): Disposable =
                object : Disposable {
                    override val isDisposed: Boolean get() = isDone

                    override fun dispose() {
                        cancel(true)
                    }
                }

            private fun wrapSchedulerTaskSafe(task: () -> Unit): Runnable =
                Runnable {
                    try {
                        task()
                    } catch (e: Throwable) {
                        handleReaktiveError(e)
                    }
                }
        }
    }
}
