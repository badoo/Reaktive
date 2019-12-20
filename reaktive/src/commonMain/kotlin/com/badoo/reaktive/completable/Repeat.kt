package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.repeat

fun Completable.repeat(count: Int = -1): Completable =
    asObservable<Nothing>()
        .repeat(count = count)
        .asCompletable()
