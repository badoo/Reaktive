@file:Suppress("MatchingDeclarationName")

package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndUpdate
import com.badoo.reaktive.utils.atomic.update

fun <T> Observable<T>.debounce(timeoutMillis: Long, scheduler: Scheduler): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        subscribe(
            object : ObservableObserver<T> {
                private val pendingValue = AtomicReference<DebouncePendingValue<T>?>(null)

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
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
                        pendingValue.getAndUpdate { null }?.let { emitter.onNext(it.value) }
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

internal class DebouncePendingValue<T>(
    val value: T
)
