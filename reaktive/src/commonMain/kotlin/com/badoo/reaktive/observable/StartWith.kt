package com.badoo.reaktive.observable

fun <T> Observable<T>.startWith(other: Observable<T>): Observable<T> = concat(other, this)

fun <T> Observable<T>.startWithValue(value: T): Observable<T> = concat(value.toObservable(), this)