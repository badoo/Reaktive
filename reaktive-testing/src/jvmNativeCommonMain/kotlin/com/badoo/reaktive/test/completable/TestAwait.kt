package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.blockingAwait

actual fun Completable.testAwait() {
    blockingAwait()
}
