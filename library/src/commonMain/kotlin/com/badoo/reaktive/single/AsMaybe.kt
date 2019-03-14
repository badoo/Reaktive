package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T> Single<T>.asMaybe(): Maybe<T> =
    maybe { observer ->
        subscribeSafe(observer)
    }