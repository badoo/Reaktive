package com.badoo.reaktive.observable

fun <T> Observable<Iterable<T>>.flatten(): Observable<T> = concatMap(Iterable<T>::asObservable)