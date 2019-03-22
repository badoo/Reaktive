package com.badoo.reaktive.completable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T> Completable.asMaybe(): Maybe<T> =
    maybe { observer ->
        subscribeSafe(observer)
    }