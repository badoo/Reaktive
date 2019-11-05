package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicReference

fun <T> Observable<T>.sample(windowMillis: Long, scheduler: Scheduler): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        subscribe(
            object : ObservableObserver<T> {
                private val lastValue = AtomicReference<SampleLastValue<T>?>(null)

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable

                    executor.submitRepeating(periodMillis = windowMillis) {
                        lastValue
                            .value
                            ?.value
                            ?.also(emitter::onNext)
                    }
                }

                override fun onNext(value: T) {
                    lastValue.value = SampleLastValue(value)
                }

                override fun onComplete() {
                    executor.cancel()
                    executor.submit(task = emitter::onComplete)
                }

                override fun onError(error: Throwable) {
                    executor.cancel()
                    executor.submit { emitter.onError(error) }
                }
            }
        )
    }

private class SampleLastValue<T>(
    val value: T
)
