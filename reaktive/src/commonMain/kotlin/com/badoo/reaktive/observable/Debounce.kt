package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

fun <T> Observable<T>.debounce(timeoutMillis: Long, scheduler: Scheduler): Observable<T> =
    observable { emitter ->
        val disposableWrapper = CompositeDisposable()
        emitter.setDisposable(disposableWrapper)
        val executor = scheduler.newExecutor()
        disposableWrapper += executor

        subscribeSafe(
            object : ObservableObserver<T> {
                private val pendingValue = AtomicReference<DebouncePendingValue<T>?>(null, true)

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper += disposable
                }

                override fun onNext(value: T) {
                    val newPendingValue = DebouncePendingValue(value)
                    pendingValue.value = newPendingValue

                    executor.cancel()

                    executor.submit(timeoutMillis) {
                        pendingValue.update {
                            if (it === newPendingValue) null else it
                        }

                        emitter.onNext(value)
                    }
                }

                override fun onComplete() {
                    executor.cancel()

                    executor.submit {
                        pendingValue
                            .value
                            ?.value
                            ?.also(emitter::onNext)

                        emitter.onComplete()
                    }
                }

                override fun onError(error: Throwable) {
                    executor.cancel()

                    executor.submit {
                        emitter.onError(error)
                    }
                }
            }
        )
    }

private class DebouncePendingValue<T>(
    val value: T
)