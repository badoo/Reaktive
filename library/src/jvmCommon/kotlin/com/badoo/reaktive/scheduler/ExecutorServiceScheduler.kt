package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

internal class ExecutorServiceScheduler(
    private val executorServiceStrategy: ExecutorServiceStrategy
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor =
        ExecutorImpl(executorServiceStrategy)
            .also(disposables::add)

    override fun destroy() {
        disposables.dispose()
        executorServiceStrategy.destroy()
    }

    private class ExecutorImpl(
        private val executorServiceStrategy: ExecutorServiceStrategy
    ) : Scheduler.Executor {

        @Volatile
        private var executor: ScheduledExecutorService? = executorServiceStrategy.get()

        private val disposables = CompositeDisposable()
        private val monitor: Any = disposables
        override val isDisposed: Boolean get() = executor == null

        override fun dispose() {
            if (executor != null) {
                val executorToRecycle: ScheduledExecutorService
                synchronized(monitor) {
                    executorToRecycle = executor ?: return
                    executor = null
                }
                disposables.dispose()
                executorServiceStrategy.recycle(executorToRecycle)
            }
        }

        override fun submit(delay: Long, task: () -> Unit) {
            executeIfNotRecycled {
                it.schedule(task, delay, TimeUnit.MILLISECONDS)
            }
                ?.toDisposable()
                ?.also(disposables::add)
        }

        override fun submitRepeating(startDelay: Long, period: Long, task: () -> Unit) {
            executeIfNotRecycled {
                it.scheduleAtFixedRate(task, startDelay, period, TimeUnit.MILLISECONDS)
            }
                ?.toDisposable()
                ?.also(disposables::add)
        }

        override fun cancel() {
            disposables.clear(true)
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
                    override val isDisposed: Boolean = isDone

                    override fun dispose() {
                        cancel(true)
                    }
                }
        }
    }
}