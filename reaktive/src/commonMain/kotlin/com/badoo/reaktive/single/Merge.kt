package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.flatMapSingle
import com.badoo.reaktive.observable.merge

/**
 * Merges multiple [Single]s into one [Observable], running all [Single]s simultaneously.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#merge-java.lang.Iterable-).
 */
fun <T> Iterable<Single<T>>.merge(): Observable<T> =
    asObservable()
        .flatMapSingle { it }

/**
 * Merges multiple [Single]s into one [Observable], running all [Single]s simultaneously.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#merge-java.lang.Iterable-).
 */
fun <T> merge(vararg sources: Single<T>): Observable<T> =
    sources
        .map(Single<T>::asObservable)
        .merge()
