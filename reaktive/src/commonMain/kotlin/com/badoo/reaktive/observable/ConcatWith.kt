package com.badoo.reaktive.observable

fun <T> Observable<T>.concatWith(other: Observable<T>): Observable<T> = concat(this, other)

fun <T> Observable<T>.concatWithValue(value: T): Observable<T> = concat(this, value.toObservable())
