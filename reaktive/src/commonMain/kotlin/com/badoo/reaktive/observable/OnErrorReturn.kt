package com.badoo.reaktive.observable

fun <T> Observable<T>.onErrorReturn(valueSupplier: (Throwable) -> T): Observable<T> =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toObservable() }

fun <T> Observable<T>.onErrorReturnValue(value: T): Observable<T> =
    onErrorResumeNext { value.toObservable() }
    