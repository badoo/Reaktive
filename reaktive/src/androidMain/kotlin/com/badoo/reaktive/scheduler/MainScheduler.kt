package com.badoo.reaktive.scheduler

import android.os.Handler
import android.os.Looper
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.coerceAtLeastZero
import kotlin.time.Duration

internal class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = ExecutorImpl(disposables)

    override fun destroy() {
        disposables.dispose()
    }

    private class ExecutorImpl(
        private val disposables: CompositeDisposable
    ) : Scheduler.Executor {
        @Volatile
        private var handler: Handler? = Handler(Looper.getMainLooper())

        private val monitor = Any()
        override val isDisposed: Boolean get() = handler == null

        init {
            disposables += this
        }

        override fun dispose() {
            if (handler != null) {
                val handlerToCancel: Handler
                synchronized(monitor) {
                    handlerToCancel = handler ?: return
                    handler = null
                }
                handlerToCancel.removeCallbacksAndMessages(null)
                disposables -= this
            }
        }

        override fun submit(delay: Duration, period: Duration, task: () -> Unit) {
            val runnable =
                object : Runnable {
                    override fun run() {
                        if (!period.isInfinite()) {
                            executeIfNotRecycled {
                                it.postDelayed(this, period.coerceAtLeastZero().inWholeMilliseconds)
                            }
                        }
                        task()
                    }
                }

            executeIfNotRecycled {
                it.postDelayed(runnable, delay.coerceAtLeastZero().inWholeMilliseconds)
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
