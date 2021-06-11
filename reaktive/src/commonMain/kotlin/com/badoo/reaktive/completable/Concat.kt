package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concatMap

/**
 * Concatenates multiple [Completable] sources one by one into a [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#concat-java.lang.Iterable-).
 */
fun Iterable<Completable>.concat(): Completable =
    asObservable()
        .concatMap { it.asObservable<Nothing>() }
        .asCompletable()

/**
 * Concatenates multiple [Completable] sources one by one into a [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#concatArray-io.reactivex.CompletableSource...-).
 */
fun concat(vararg sources: Completable): Completable =
    sources
        .asList()
        .concat()
