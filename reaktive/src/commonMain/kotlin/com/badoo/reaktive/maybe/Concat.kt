package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concat
import com.badoo.reaktive.observable.concatMap

/**
 * Concatenates multiple [Maybe] sources one by one into an [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#concat-java.lang.Iterable-).
 */
fun <T> Iterable<Maybe<T>>.concat(): Observable<T> =
    asObservable()
        .concatMap { it.asObservable() }

/**
 * Concatenates multiple [Maybe] sources one by one into an [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#concatArray-io.reactivex.MaybeSource...-).
 */
fun <T> concat(vararg sources: Maybe<T>): Observable<T> =
    sources
        .map(Maybe<T>::asObservable)
        .concat()
