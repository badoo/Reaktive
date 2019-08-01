package com.badoo.reaktive.maybe

fun <T> Maybe<T>.onErrorReturn(valueSupplier: (Throwable) -> T) =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toMaybe() }

fun <T> Maybe<T>.onErrorReturnValue(value: T) =
    onErrorResumeNext { value.toMaybe() }