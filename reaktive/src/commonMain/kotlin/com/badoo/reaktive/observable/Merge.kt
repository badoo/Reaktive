package com.badoo.reaktive.observable

/**
 * Merges multiple [Observable]s into one [Observable], running all [Observable]s simultaneously.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#merge-java.lang.Iterable-).
 */
fun <T> Iterable<Observable<T>>.merge(): Observable<T> =
    asObservable()
        .flatMap { it }

/**
 * Merges multiple [Observable]s into one [Observable], running all [Observable]s simultaneously.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#mergeArray-io.reactivex.ObservableSource...-).
 */
fun <T> merge(vararg sources: Observable<T>): Observable<T> =
    sources
        .asIterable()
        .merge()
