package com.badoo.reaktive.scheduler

import android.os.Handler
import android.os.Looper
import com.badoo.reaktive.disposable.CompositeDisposable

internal class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor =
        ExecutorImpl()
            .also(disposables::add)

    override fun destroy() {
        disposables.dispose()
    }

    private class ExecutorImpl : Scheduler.Executor {
        @Volatile
        private var handler: Handler? = Handler(Looper.getMainLooper())

        private val monitor = Any()
        override val isDisposed: Boolean get() = handler == null

        override fun dispose() {
            if (handler != null) {
                val handlerToCancel: Handler
                synchronized(monitor) {
                    handlerToCancel = handler ?: return
                    handler = null
                }
                handlerToCancel.removeCallbacksAndMessages(null)
            }
        }

        override fun submit(delay: Long, task: () -> Unit) {
            executeIfNotRecycled {
                it.postDelayed(task, delay)
            }
        }

        override fun submitRepeating(startDelay: Long, period: Long, task: () -> Unit) {
            val runnable =
                object : Runnable {
                    override fun run() {
                        executeIfNotRecycled {
                            it.postDelayed(this, period)
                        }
                        task()
                    }
                }

            executeIfNotRecycled {
                it.postDelayed(runnable, startDelay)
            }
        }

        override fun cancel() {
            executeIfNotRecycled {
                it.removeCallbacksAndMessages(null)
            }
        }

        private inline fun <T> executeIfNotRecycled(block: (Handler) -> T): T? {
            if (handler != null) {
                synchronized(monitor) {
                    return handler?.let(block)
                }
            }

            return null
        }
    }
}