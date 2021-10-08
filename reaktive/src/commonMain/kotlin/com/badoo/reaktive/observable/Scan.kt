package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.Uninitialized

/**
 * Returns an [Observable] that subscribes to the source [Observable] and calls the [accumulate] function
 * with a result of a previous [accumulate] invocation and a current element.
 * The returned [Observable] emits every value returned by the [accumulate] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#scan-io.reactivex.functions.BiFunction-).
 */
fun <T> Observable<T>.scan(accumulate: (acc: T, value: T) -> T): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                var cache = ObjectReference<Any?>(Uninitialized)

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

/**
 * Returns an [Observable] that subscribes to the source [Observable] and calls the [accumulator] function
 * first with the specified [seed] value and a first element emitted by the source [Observable].
 * Then for every subsequent element emitted by the source [Observable], calls [accumulator] with
 * its previous result the emitted element. The returned [Observable] emits every value returned by
 * the [accumulator] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#scan-R-io.reactivex.functions.BiFunction-).
 */
fun <T, R> Observable<T>.scan(seed: R, accumulator: (acc: R, value: T) -> R): Observable<R> =
    scan<T, R>({ seed }, accumulator)

/**
 * Returns an [Observable] that subscribes to the source [Observable] and calls the [accumulate] function
 * first with a value returned by [getSeed] function and a first element emitted by the source [Observable].
 * Then for every subsequent element emitted by the source [Observable], calls [accumulate] with
 * its previous result the emitted element. The returned [Observable] emits every value returned by
 * the [accumulate] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#scanWith-java.util.concurrent.Callable-io.reactivex.functions.BiFunction-).
 */
fun <T, R> Observable<T>.scan(getSeed: () -> R, accumulate: (acc: R, value: T) -> R): Observable<R> =
    observable { emitter ->
        val cache: ObjectReference<R> =
            getSeed()
                .also(emitter::onNext)
                .let(::ObjectReference)

        subscribe(
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
