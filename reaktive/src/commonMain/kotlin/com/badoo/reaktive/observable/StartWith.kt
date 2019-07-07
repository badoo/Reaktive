package com.badoo.reaktive.observable

fun <T> Observable<T>.starWith(other: Observable<T>): Observable<T> = concat(other, this)