package com.badoo.reaktive.observable

/**
 * Returns an [Observable] that emits the elements emitted from the source [Observable]
 * or the specified [defaultValue] if the source [Observable] is empty.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#defaultIfEmpty-T-).
 */
fun <T> Observable<T>.defaultIfEmpty(defaultValue: T): Observable<T> =
    switchIfEmpty(observableOf(defaultValue))
