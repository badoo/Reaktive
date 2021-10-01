package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single

/**
 * Returns a [Single] that emits only the very first element emitted by the source [Observable],
 * or signals [NoSuchElementException] if the source [Observable] is empty.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#firstOrError--).
 */
fun <T> Observable<T>.firstOrError(error: Throwable = NoSuchElementException()): Single<T> =
    firstOrAction { emitter ->
        emitter.onError(error)
    }

/**
 * Returns a [Single] that emits only the very first element emitted by the source [Observable],
 * or signals a [Throwable] returned by [errorSupplier] if the source [Observable] is empty.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#firstOrError--).
 */
fun <T> Observable<T>.firstOrError(errorSupplier: () -> Throwable): Single<T> =
    firstOrAction { emitter ->
        try {
            errorSupplier()
        } catch (e: Throwable) {
            e
        }
            .also(emitter::onError)
    }
