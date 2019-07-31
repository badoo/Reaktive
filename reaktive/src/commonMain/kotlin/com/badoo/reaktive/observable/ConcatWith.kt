package com.badoo.reaktive.observable

fun <T> Observable<T>.concatWith(other: Observable<T>): Observable<T> = concat(this, other)

fun <T> Observable<T>.concatWith(item: T): Observable<T> = concat(this, item.toObservable())