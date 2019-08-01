package com.badoo.reaktive.single

fun <T> Single<T>.onErrorReturn(valueSupplier: (Throwable) -> T) =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toSingle() }

fun <T> Single<T>.onErrorReturnValue(value: T) =
    onErrorResumeNext { value.toSingle() }