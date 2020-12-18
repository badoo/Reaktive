package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concatMap

fun Iterable<Completable>.concat(): Completable =
    asObservable()
        .concatMap { it.asObservable<Nothing>() }
        .asCompletable()

fun concat(vararg sources: Completable): Completable =
    sources
        .asList()
        .concat()
