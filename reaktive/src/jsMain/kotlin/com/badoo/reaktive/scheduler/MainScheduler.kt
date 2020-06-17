package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.Timers

internal class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = MainThreadExecutor(disposables)

    override fun destroy() = disposables.dispose()

    private class MainThreadExecutor(
        private val disposables: CompositeDisposable
    ) : Scheduler.Executor {

        private var _isDisposed = false

        private val timeoutIds = mutableListOf<Long>()
        private val intervalIds = mutableListOf<Long>()

        init {
            disposables += this
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            timeoutIds += Timers.setTimeout(delayMillis, task).toLong()
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            if (startDelayMillis != 0L) {
                timeoutIds += Timers.setTimeout(startDelayMillis, {
                    intervalIds += Timers.setInterval(periodMillis, task).toLong()
                }).toLong()
            } else {
                intervalIds += Timers.setInterval(periodMillis, task).toLong()
            }
        }

        override fun cancel() {
            timeoutIds.forEach(Timers::clearTimeout)
            intervalIds.forEach(Timers::clearInterval)
        }

        override val isDisposed: Boolean
            get() = _isDisposed

        override fun dispose() {
            cancel()
            _isDisposed = true
            disposables -= this
        }
    }
}
