package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.utils.atomic.AtomicInt

/**
 * When the [Single] signals `onSuccess`, re-subscribes to the [Single] if the [predicate] function returns `false`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#repeatUntil-io.reactivex.functions.BooleanSupplier-).
 */
fun <T> Single<T>.repeatUntil(predicate: (T) -> Boolean): Observable<T> =
    observable { emitter ->
        val observer =
            object : SingleObserver<T>, ErrorCallback by emitter {
                private val recursiveGuard = AtomicInt()

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.onNext(value)

                    emitter.tryCatch(
                        block = { predicate(value) },
                        onSuccess = { shouldComplete ->
                            if (shouldComplete) {
                                emitter.onComplete()
                            } else if (!emitter.isDisposed) {
                                subscribeToUpstream()
                            }
                        }
                    )
                }

                fun subscribeToUpstream() {
                    // Prevents recursive subscriptions
                    if (recursiveGuard.addAndGet(1) == 1) {
                        do {
                            subscribe(this)
                        } while (recursiveGuard.addAndGet(-1) > 0)
                    }
                }
            }
        observer.subscribeToUpstream()
    }
