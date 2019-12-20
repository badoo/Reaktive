package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.NANOS_IN_MILLI
import platform.Foundation.NSThread
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.freeze

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

        override fun submit(delayMillis: Long, task: () -> Unit) {
            operations.purge()
            val operation = Operation(task).addTo(operations)
            dispatch_after(
                dispatch_time(DISPATCH_TIME_NOW, delayMillis.toNanos()),
                dispatch_get_main_queue(),
                operation
            )
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            operations.purge()
            val operation =
                Operation {
                    task()
                    submitRepeating(periodMillis, periodMillis, task)
                }.addTo(operations)
            dispatch_after(
                dispatch_time(DISPATCH_TIME_NOW, startDelayMillis.toNanos()),
                dispatch_get_main_queue(),
                operation
            )
        }

        override fun cancel() {
            operations.clear()
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

            private val taskReference = FreezableAtomicReference<(() -> Unit)?>(task)

            init {
                if (!NSThread.isMainThread) {
                    freeze()
                }
            }

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

    private companion object {
        private fun Long.toNanos(): Long = this * NANOS_IN_MILLI
    }
}
