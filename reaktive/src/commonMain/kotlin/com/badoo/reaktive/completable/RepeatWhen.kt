package com.badoo.reaktive.completable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.repeatWhen

fun Completable.repeatWhen(handler: (repeatNumber: Int) -> Maybe<*>): Completable =
    asObservable<Nothing>()
        .repeatWhen(handler)
        .asCompletable()
