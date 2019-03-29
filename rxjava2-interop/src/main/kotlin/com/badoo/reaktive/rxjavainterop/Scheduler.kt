package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.scheduler.Scheduler
import java.util.concurrent.TimeUnit

fun io.reactivex.Scheduler.toReaktive(): Scheduler =
    object : Scheduler {
        private val disposables = CompositeDisposable()

        override fun newExecutor(): Scheduler.Executor =
            this@toReaktive
                .createWorker()
                .toExecutor()
                .also(disposables::add)

        override fun destroy() {
            disposables.dispose()
            this@toReaktive.shutdown()
        }
    }

private fun io.reactivex.Scheduler.Worker.toExecutor(): Scheduler.Executor =
    object : Scheduler.Executor {
        private val disposables = CompositeDisposable()
        override val isDisposed: Boolean get() = disposables.isDisposed

        override fun dispose() {
            disposables.dispose()
            this@toExecutor.dispose()
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            disposables +=
                this@toExecutor
                    .schedule(task, delayMillis, TimeUnit.MILLISECONDS)
                    .toReaktive()
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            disposables +=
                this@toExecutor
                    .schedulePeriodically(task, startDelayMillis, periodMillis, TimeUnit.MILLISECONDS)
                    .toReaktive()
        }

        override fun cancel() {
            disposables.clear()
        }
    }