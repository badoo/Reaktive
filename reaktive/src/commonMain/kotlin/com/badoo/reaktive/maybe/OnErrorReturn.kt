package com.badoo.reaktive.maybe

fun <T> Maybe<T>.onErrorReturn(valueSupplier: (Throwable) -> T): Maybe<T> =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toMaybe() }

fun <T> Maybe<T>.onErrorReturnValue(value: T): Maybe<T> =
    onErrorResumeNext { value.toMaybe() }