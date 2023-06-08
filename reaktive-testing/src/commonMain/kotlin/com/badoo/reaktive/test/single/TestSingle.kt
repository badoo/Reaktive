package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleCallbacks
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.test.base.TestSource

class TestSingle<T> : TestSource<SingleObserver<T>>(), Single<T>, SingleCallbacks<T> {

    override fun onSuccess(value: T) {
        onEvent { it.onSuccess(value) }
    }
}
