package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.coerceAtLeastZero
import kotlin.time.Duration

internal class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor = MainThreadExecutor(disposables)

    override fun destroy() = disposables.dispose()

    private class MainThreadExecutor(
        private val disposables: CompositeDisposable,
    ) : Scheduler.Executor {

        private var _isDisposed = false

        private val timeoutIds = mutableSetOf<TimeoutId>()
        private val intervalIds = mutableSetOf<TimeoutId>()

        init {
            disposables += this
        }

        override fun submit(delay: Duration, period: Duration, task: () -> Unit) {
            if (isDisposed) {
                return
            }

            if (period.isInfinite()) {
                setTimeout(delay = delay, task = task)
                return
            }

            if (delay.isPositive()) {
                setTimeout(delay = delay) {
                    setInterval(period = period, task)
                }
                return
            }

            setInterval(period = period, task)
        }

        private fun setTimeout(delay: Duration, task: () -> Unit) {
            var timeoutId: TimeoutId? = null

            timeoutId = jsSetTimeout(
                task = {
                    timeoutIds.remove(timeoutId)
                    task()
                },
                delayMillis = delay.coerceAtLeastZero().inWholeMilliseconds.toInt()
            )

            timeoutIds.add(timeoutId)
        }

        private fun setInterval(period: Duration, task: () -> Unit) {
            intervalIds.add(
                jsSetInterval(
                    task = task,
                    delayMillis = period.coerceAtLeastZero().inWholeMilliseconds.toInt(),
                )
            )
        }

        override fun cancel() {
            timeoutIds.forEach { jsClearTimeout(it) }
            intervalIds.forEach { jsClearInterval(it) }
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
