package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import java.util.concurrent.TimeUnit

fun io.reactivex.rxjava3.core.Scheduler.asReaktiveScheduler(): Scheduler =
    object : Scheduler {
        private val disposables = CompositeDisposable()

        override fun newExecutor(): Scheduler.Executor =
            this@asReaktiveScheduler
                .createWorker()
                .asExecutor(disposables)

        override fun destroy() {
            disposables.dispose()
            this@asReaktiveScheduler.shutdown()
        }
    }

@Deprecated(
    message = "Use asReaktiveScheduler",
    replaceWith = ReplaceWith("asReaktiveScheduler()"),
    level = DeprecationLevel.ERROR
)
fun io.reactivex.rxjava3.core.Scheduler.asReaktive(): Scheduler = asReaktiveScheduler()

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
                    .asReaktiveDisposable()
        }

        override fun submitRepeating(startDelayMillis: Long, periodMillis: Long, task: () -> Unit) {
            taskDisposables.purge()

            taskDisposables +=
                this@asExecutor
                    .schedulePeriodically(task, startDelayMillis, periodMillis, TimeUnit.MILLISECONDS)
                    .asReaktiveDisposable()
        }

        override fun cancel() {
            taskDisposables.clear()
        }
    }
