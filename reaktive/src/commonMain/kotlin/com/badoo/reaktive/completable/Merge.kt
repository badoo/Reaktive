package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.flatMapCompletable

fun Iterable<Completable>.merge(): Completable =
    asObservable()
        .flatMapCompletable { it }

fun merge(vararg sources: Completable): Completable =
    sources
        .asIterable()
        .merge()
        