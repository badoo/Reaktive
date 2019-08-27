package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.scheduler.Scheduler
import java.util.concurrent.TimeUnit

fun io.reactivex.Scheduler.asReaktive(): Scheduler =
    object : Scheduler {
        private val disposables = CompositeDisposable()

        override fun newExecutor(): Scheduler.Executor =
            this@asReaktive
                .createWorker()
                .asExecutor()
                .also(disposables::add)

        override fun destroy() {
            disposables.dispose()
            this@asReaktive.shutdown()
        }
    }

private fun io.reactivex.Scheduler.Worker.asExecutor(): Scheduler.Executor =
    object : Scheduler.Executor {
        private val disposables = CompositeDisposable()
        override val isDisposed: Boolean get() = disposables.isDisposed

        override fun dispose() {
            disposables.dispose()
            this@asExecutor.dispose()
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            disposables +=
                this@asExecutor
                    .schedule(task, delayMillis, TimeUnit.MILLISECONDS)
                    .asReaktive()
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            disposables +=
                this@asExecutor
                    .schedulePeriodically(task, startDelayMillis, periodMillis, TimeUnit.MILLISECONDS)
                    .asReaktive()
        }

        override fun cancel() {
            disposables.clear()
        }
    }