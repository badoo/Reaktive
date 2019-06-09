package com.badoo.reaktive.test.maybe

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeCallbacks
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.test.base.TestSource

class TestMaybe<T> : TestSource<MaybeObserver<T>>(), Maybe<T>, MaybeCallbacks<T> {

    override fun onSuccess(value: T) {
        onEvent { it.onSuccess(value) }
    }

    override fun onComplete() {
        onEvent(MaybeObserver<*>::onComplete)
    }
}