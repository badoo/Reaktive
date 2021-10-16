package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single

/**
 * Converts this [Maybe] into a [Single], which signals a success value via `onSuccess` (if this [Maybe] succeeds)
 * or [error] via `onError` (if this [Maybe] completes).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#toSingle--).
 */
fun <T> Maybe<T>.asSingleOrError(error: Throwable = NoSuchElementException()): Single<T> =
    asSingleOrAction { observer ->
        observer.onError(error)
    }

/**
 * Converts this [Maybe] into a [Single], which signals a success value via `onSuccess` (if this [Maybe] succeeds)
 * or a [Throwable] returned by [errorSupplier] via `onError` (if this [Maybe] completes).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#toSingle--).
 */
fun <T> Maybe<T>.asSingleOrError(errorSupplier: () -> Throwable): Single<T> =
    asSingleOrAction { observer ->
        try {
            errorSupplier()
        } catch (e: Throwable) {
            e
        }
            .also(observer::onError)
    }
