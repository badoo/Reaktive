package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import kotlin.browser.window

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
            timeoutIds += window.setTimeout(task, delayMillis.toInt())
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            if (startDelayMillis != 0L) {
                timeoutIds += window.setTimeout({
                    intervalIds += window.setInterval(task, periodMillis.toInt())
                }, startDelayMillis.toInt())
            } else {
                intervalIds += window.setInterval(task, periodMillis.toInt())
            }
        }

        override fun cancel() {
            timeoutIds.forEach(window::clearTimeout)
            intervalIds.forEach(window::clearInterval)
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
