package com.badoo.reaktive.observable

/**
 * Returns an [Observable] that emits elements that are the result of applying the specified [mapper] function
 * to pairs of values, one each from the source [Observable] and another from the specified [other][other] [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#zipWith-io.reactivex.ObservableSource-io.reactivex.functions.BiFunction-).
 */
fun <T, R, I> Observable<T>.zipWith(other: Observable<R>, mapper: (T, R) -> I): Observable<I> =
    zip(this, other) { first, second -> mapper(first, second) }
