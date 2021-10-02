package com.badoo.reaktive.observable

/**
 * Concatenates elements of each [Observable] into a single [Observable] without interleaving them.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#concat-java.lang.Iterable-).
 */
fun <T> Iterable<Observable<T>>.concat(): Observable<T> =
    asObservable()
        .concatMap { it }

/**
 * Concatenates elements of each [Observable] into a single [Observable] without interleaving them.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#concatArray-io.reactivex.ObservableSource...-).
 */
fun <T> concat(vararg sources: Observable<T>): Observable<T> =
    sources
        .asList()
        .concat()
