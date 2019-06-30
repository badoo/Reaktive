package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.atomic.AtomicReference

fun <T> Observable<T>.distinctUntilChanged(comparator: (T, T) -> Boolean = ::equals): Observable<T> =
    distinctUntilChanged({ it }, comparator)

fun <T, R> Observable<T>.distinctUntilChanged(
    keySelector: (T) -> R,
    comparator: (R, R) -> Boolean = ::equals
): Observable<T> =
    observable { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                val cache = AtomicReference<Any?>(Uninitialized, true)

                override fun onNext(value: T) {
                    val previous = cache.value
                    cache.value = value

                    val next =
                        try {
                            @Suppress("UNCHECKED_CAST")
                            if ((previous === Uninitialized) || !comparator(keySelector(previous as T), keySelector(value))) {
                                value
                            } else {
                                return
                            }
                        } catch (e: Throwable) {
                            emitter.onError(e)
                            return
                        }

                    emitter.onNext(next)
                }

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }

private fun equals(a: Any?, b: Any?): Boolean = a == b