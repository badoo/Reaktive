package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.global.external.globalThis

internal class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = MainThreadExecutor(disposables)

    override fun destroy() = disposables.dispose()

    private class MainThreadExecutor(
        private val disposables: CompositeDisposable
    ) : Scheduler.Executor {

        private var _isDisposed = false

        private val timeoutIds = mutableListOf<dynamic>()
        private val intervalIds = mutableListOf<dynamic>()

        init {
            disposables += this
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            timeoutIds.add(globalThis.setTimeout(task, delayMillis.toInt()))
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            if (startDelayMillis != 0L) {
                timeoutIds.add(globalThis.setTimeout({
                    intervalIds.add(globalThis.setInterval(task, periodMillis.toInt()))
                }, startDelayMillis.toInt()))
            } else {
                intervalIds.add(globalThis.setInterval(task, periodMillis.toInt()))
            }
        }

        override fun cancel() {
            timeoutIds.forEach { globalThis.clearTimeout(it) }
            intervalIds.forEach { globalThis.clearInterval(it) }
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
