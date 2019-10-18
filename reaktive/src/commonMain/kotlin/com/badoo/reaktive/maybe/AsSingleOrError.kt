package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single

fun <T> Maybe<T>.asSingleOrError(error: Throwable = NoSuchElementException()): Single<T> =
    asSingleOrAction { observer ->
        observer.onError(error)
    }

fun <T> Maybe<T>.asSingleOrError(errorSupplier: () -> Throwable): Single<T> =
    asSingleOrAction { observer ->
        try {
            errorSupplier()
        } catch (e: Throwable) {
            e
        }
            .also(observer::onError)
    }
