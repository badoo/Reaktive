package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.timer.clearInterval
import com.badoo.reaktive.timer.clearTimeout
import com.badoo.reaktive.timer.setInterval
import com.badoo.reaktive.timer.setTimeout

internal class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = MainThreadExecutor(disposables)

    override fun destroy() = disposables.dispose()

    private class MainThreadExecutor(
        private val disposables: CompositeDisposable
    ) : Scheduler.Executor {

        private var _isDisposed = false

        private val timeoutIds = mutableListOf<Int>()
        private val intervalIds = mutableListOf<Int>()

        init {
            disposables += this
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            timeoutIds += setTimeout(task, delayMillis.toInt())
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            if (startDelayMillis != 0L) {
                timeoutIds += setTimeout({
                    intervalIds += setInterval(task, periodMillis.toInt())
                }, startDelayMillis.toInt())
            } else {
                intervalIds += setInterval(task, periodMillis.toInt())
            }
        }

        override fun cancel() {
            timeoutIds.forEach(::clearTimeout)
            intervalIds.forEach(::clearInterval)
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
