package com.badoo.reaktive.observable

/**
 * Returns an [Observable] that concatenates the [other] and the source [Observable]s.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#startWith-io.reactivex.ObservableSource-).
 */
fun <T> Observable<T>.startWith(other: Observable<T>): Observable<T> = concat(other, this)

/**
 * Returns an [Observable] that first emits the specified [value] and then subscribes to the source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#startWith-T-).
 */
fun <T> Observable<T>.startWithValue(value: T): Observable<T> = concat(value.toObservable(), this)
