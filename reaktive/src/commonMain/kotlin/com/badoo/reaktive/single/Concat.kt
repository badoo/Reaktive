package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concat
import com.badoo.reaktive.observable.concatMap

/**
 * Concatenates multiple [Single] sources one by one into an [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#concat-java.lang.Iterable-).
 */
fun <T> Iterable<Single<T>>.concat(): Observable<T> =
    asObservable()
        .concatMap { it.asObservable() }

/**
 * Concatenates multiple [Single] sources one by one into an [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#concatArray-io.reactivex.SingleSource...-).
 */
fun <T> concat(vararg sources: Single<T>): Observable<T> =
    sources
        .map(Single<T>::asObservable)
        .concat()
