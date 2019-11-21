package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import java.util.concurrent.TimeUnit

fun io.reactivex.rxjava3.core.Scheduler.asReaktive(): Scheduler =
    object : Scheduler {
        private val disposables = CompositeDisposable()

        override fun newExecutor(): Scheduler.Executor =
            this@asReaktive
                .createWorker()
                .asExecutor(disposables)

        override fun destroy() {
            disposables.dispose()
            this@asReaktive.shutdown()
        }
    }

private fun io.reactivex.rxjava3.core.Scheduler.Worker.asExecutor(disposables: CompositeDisposable): Scheduler.Executor =
    object : Scheduler.Executor {
        private val taskDisposables = CompositeDisposable()
        override val isDisposed: Boolean get() = taskDisposables.isDisposed

        init {
            disposables += this
        }

        override fun dispose() {
            taskDisposables.dispose()
            this@asExecutor.dispose()
            disposables -= this
        }

        override fun submit(delayMillis: Long, task: () -> Unit) {
            taskDisposables.purge()

            taskDisposables +=
                this@asExecutor
                    .schedule(task, delayMillis, TimeUnit.MILLISECONDS)
                    .asReaktive()
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            taskDisposables.purge()

            taskDisposables +=
                this@asExecutor
                    .schedulePeriodically(task, startDelayMillis, periodMillis, TimeUnit.MILLISECONDS)
                    .asReaktive()
        }

        override fun cancel() {
            taskDisposables.clear()
        }
    }
