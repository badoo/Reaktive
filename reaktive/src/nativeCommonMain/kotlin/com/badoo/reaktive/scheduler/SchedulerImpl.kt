package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.looperthread.LooperThreadStrategy
import kotlin.native.concurrent.AtomicInt
import kotlin.system.getTimeMillis
import kotlin.time.Duration.Companion.milliseconds

internal class SchedulerImpl(
    private val looperThreadStrategy: LooperThreadStrategy
) : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = ExecutorImpl(disposables, looperThreadStrategy)

    override fun destroy() {
        disposables.dispose()
        looperThreadStrategy.destroy()
    }

    private class ExecutorImpl(
        private val disposables: CompositeDisposable,
        private val looperThreadStrategy: LooperThreadStrategy
    ) : Scheduler.Executor {

        private val looperThread = looperThreadStrategy.get()
        private val _isDisposed = AtomicInt(0)
        override val isDisposed: Boolean get() = _isDisposed.value != 0

        init {
            disposables += this
        }

        override fun dispose() {
            _isDisposed.value = 1
            cancel()
            looperThreadStrategy.recycle(looperThread)
            disposables -= this
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            if (!isDisposed) {
                looperThread.schedule(this, getStartTimeMillis(delayMillis).milliseconds, task)
            }
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            lateinit var t: () -> Unit
            t = {
                if (!isDisposed) {
                    val nextStartTimeMillis = getStartTimeMillis(periodMillis)
                    task()
                    if (!isDisposed) {
                        looperThread.schedule(this, nextStartTimeMillis.milliseconds, t)
                    }
                }
            }
            if (!isDisposed) {
                looperThread.schedule(this, getStartTimeMillis(startDelayMillis).milliseconds, t)
            }
        }

        override fun cancel() {
            looperThread.cancel(this)
        }

        private companion object {
            private fun getStartTimeMillis(delayMillis: Long): Long = getTimeMillis() + delayMillis
        }
    }
}
