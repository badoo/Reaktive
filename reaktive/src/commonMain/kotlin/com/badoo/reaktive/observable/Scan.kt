package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.atomic.AtomicReference

fun <T> Observable<T>.scan(accumulate: (acc: T, value: T) -> T): Observable<T> =
    observable { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                var cache = AtomicReference<Any?>(Uninitialized)

                override fun onNext(value: T) {
                    val previous = cache.value

                    val next =
                        if (previous == Uninitialized) {
                            value
                        } else {
                            try {
                                @Suppress("UNCHECKED_CAST")
                                accumulate(previous as T, value)
                            } catch (e: Throwable) {
                                emitter.onError(e)
                                return
                            }
                        }

                    cache.value = next

                    emitter.onNext(next)
                }

                override fun onSubscribe(disposable: Disposable) =
                    emitter.setDisposable(disposable)

            }
        )
    }

fun <T, R> Observable<T>.scan(seed: R, accumulator: (acc: R, value: T) -> R): Observable<R> =
    scan<T, R>({ seed }, accumulator)

fun <T, R> Observable<T>.scan(getSeed: () -> R, accumulate: (acc: R, value: T) -> R): Observable<R> =
    observable { emitter ->
        val cache: AtomicReference<R> =
            getSeed()
                .also(emitter::onNext)
                .let(::AtomicReference)

        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {

                override fun onNext(value: T) {
                    val previous = cache.value

                    val next =
                        try {
                            accumulate(previous, value)
                        } catch (e: Throwable) {
                            emitter.onError(e)
                            return
                        }

                    cache.value = next

                    emitter.onNext(next)
                }

                override fun onSubscribe(disposable: Disposable) =
                    emitter.setDisposable(disposable)
            }
        )
    }
    