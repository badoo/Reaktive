package com.badoo.reaktive.completable

import com.badoo.reaktive.maybe.blockingGet

fun Completable.blockingAwait() {
    asMaybe<Unit>().blockingGet()
}
