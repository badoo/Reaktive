package com.badoo.reaktive.observable

/**
 * Concatenates both the source and the [other][other] [Observable]s.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#concatWith-io.reactivex.ObservableSource-).
 */
fun <T> Observable<T>.concatWith(other: Observable<T>): Observable<T> = concat(this, other)

/**
 * Returns an [Observable] that first emits all elements from the source [Observable] and then the provided [value].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#concatWith-io.reactivex.ObservableSource-).
 */
fun <T> Observable<T>.concatWithValue(value: T): Observable<T> = concat(this, value.toObservable())
