package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T> Completable.asMaybe(): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : CompletableObserver, Observer by observer, CompletableCallbacks by observer {
            }
        )
    }