package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T, R> Observable<T>.scan(seed: R, accumulator: (acc: R, value: T) -> R): Observable<R> =
    scan<T, R>({ seed }, accumulator)

fun <T, R> Observable<T>.scan(getSeed: () -> R, accumulate: (acc: R, value: T) -> R): Observable<R> =
    observable { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                val lock = newLock()
                var cache: R = getSeed()

                override fun onNext(value: T) {
                    val next = try {
                        accumulate(cache, value)
                    } catch (e: Throwable) {
                        emitter.onError(e)
                        return
                    }

                    lock.synchronized {
                        cache = next
                    }

                    emitter.onNext(next)
                }

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                    emitter.onNext(cache)
                }

            }
        )
    }
