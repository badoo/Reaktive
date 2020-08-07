package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asSingle
import com.badoo.reaktive.single.asPromise

actual fun Completable.testAwait(): dynamic =
    asSingle(Unit).asPromise()
