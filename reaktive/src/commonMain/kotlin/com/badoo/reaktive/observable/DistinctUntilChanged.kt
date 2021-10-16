package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.Uninitialized

/**
 * Returns an [Observable] that emits all elements emitted by the source [Observable]
 * that are distinct from their immediate predecessors when compared with each other via the provided [comparator].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#distinctUntilChanged-io.reactivex.functions.BiPredicate-).
 */
fun <T> Observable<T>.distinctUntilChanged(comparator: (T, T) -> Boolean = ::equals): Observable<T> =
    distinctUntilChanged({ it }, comparator)

/**
 * Returns an [Observable] that emits all elements emitted by the source [Observable]
 * that are distinct from their immediate predecessors. Each emitted element is mapped to a `key`
 * using the provided [keySelector]. Each pair of sibling `keys` is compared using the provided [comparator].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#distinctUntilChanged-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.distinctUntilChanged(
    keySelector: (T) -> R,
    comparator: (R, R) -> Boolean = ::equals
): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                val cache = ObjectReference<Any?>(Uninitialized)

                override fun onNext(value: T) {
                    val previous = cache.value
                    cache.value = value

                    val next =
                        try {
                            @Suppress("UNCHECKED_CAST")
                            if (previous === Uninitialized || !comparator(
                                    keySelector(previous as T),
                                    keySelector(value)
                                )
                            ) {
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
