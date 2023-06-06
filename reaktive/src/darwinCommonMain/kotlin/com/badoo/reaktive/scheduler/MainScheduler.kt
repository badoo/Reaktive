package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.coerceAtLeastZero
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import kotlin.native.concurrent.AtomicReference
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

        private val operations = CompositeDisposable()

        init {
            disposables += this
        }

        override fun submit(delay: Duration, period: Duration, task: () -> Unit) {
            if (isDisposed) {
                return
            }

            operations.purge()

            val operation =
                if (period.isInfinite()) {
                    Operation(task)
                } else {
                    Operation {
                        task()
                        submit(delay = period, period = period, task = task)
                    }
                }

            operation.addTo(operations)
            submit(delay = delay, task = operation)
        }

        private fun submit(delay: Duration, task: () -> Unit) {
            dispatch_after(
                dispatch_time(DISPATCH_TIME_NOW, delay.coerceAtLeastZero().inWholeNanoseconds),
                dispatch_get_main_queue(),
                task,
            )
        }

        override fun cancel() {
            operations.clear(dispose = true)
        }

        override val isDisposed: Boolean
            get() = operations.isDisposed

        override fun dispose() {
            operations.dispose()
            disposables -= this
        }

        private class Operation(
            task: () -> Unit
        ) : () -> Unit, Disposable {

            private val taskReference = AtomicReference<(() -> Unit)?>(task)

            override fun invoke() {
                val task: (() -> Unit)? = taskReference.value
                taskReference.value = null
                task?.invoke()
            }

            override val isDisposed: Boolean
                get() = taskReference.value == null

            override fun dispose() {
                taskReference.value = null
            }
        }
    }
}
