package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
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

private fun io.reactivex.Scheduler.Worker.toExecutor(): Scheduler.Executor {
    val disposables = CompositeDisposable()

    return object : Scheduler.Executor, Disposable by disposables {
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
}