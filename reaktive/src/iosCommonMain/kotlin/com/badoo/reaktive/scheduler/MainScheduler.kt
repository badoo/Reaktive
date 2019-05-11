package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import kotlin.native.concurrent.AtomicReference

class MainScheduler : Scheduler {

    private val disposables = CompositeDisposable()

    override fun newExecutor(): Scheduler.Executor =
        ExecutorImpl()
            .also(disposables::add)

    override fun destroy() {
        disposables.dispose()
    }

    private class ExecutorImpl : Scheduler.Executor {

        private val operations = CompositeDisposable()

        override fun submit(delayMillis: Long, task: () -> Unit) {
            val operation = Operation(task)
                .also(operations::add)
            dispatch_after(
                dispatch_time(DISPATCH_TIME_NOW, delayMillis.toNanos()),
                dispatch_get_main_queue(),
                operation
            )
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            val operation = Operation {
                task()
                submitRepeating(periodMillis, periodMillis, task)
            }.also(operations::add)
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
        }

        private class Operation(
            task: () -> Unit
        ) : () -> Unit, Disposable {

            private val taskReference = AtomicReference<(() -> Unit)?>(task)

            override fun invoke() {
                taskReference.value?.invoke()
            }

            override val isDisposed: Boolean
                get() = taskReference.value == null

            override fun dispose() {
                taskReference.value = null
            }
        }
    }

    private companion object {
        private fun Long.toNanos(): Long = this * 1000000L
    }
}