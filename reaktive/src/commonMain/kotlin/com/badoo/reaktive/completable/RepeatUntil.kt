package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.repeatUntil

fun <T> Completable.repeatUntil(predicate: () -> Boolean): Completable =
    asObservable<Nothing>()
        .repeatUntil(predicate)
        .asCompletable()
