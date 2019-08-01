package com.badoo.reaktive.single

fun <T> Single<T>.onErrorReturn(valueSupplier: (Throwable) -> T): Single<T> =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toSingle() }

fun <T> Single<T>.onErrorReturnValue(value: T): Single<T> =
    onErrorResumeNext { value.toSingle() }