package com.badoo.reaktive.single

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T> Single<T>.asMaybe(): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : SingleObserver<T>, Observer by observer, SingleCallbacks<T> by observer {
            }
        )
    }