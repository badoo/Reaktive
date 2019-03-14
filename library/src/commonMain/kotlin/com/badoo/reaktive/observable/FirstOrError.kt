package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single

fun <T> Observable<T>.firstOrError(error: Throwable): Single<T> =
    firstOrAction { emitter ->
        emitter.onError(error)
    }

fun <T> Observable<T>.firstOrError(errorSupplier: () -> Throwable): Single<T> =
    firstOrAction { emitter ->
        try {
            errorSupplier()
        } catch (e: Throwable) {
            e
        }
            .also(emitter::onError)
    }