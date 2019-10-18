package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleCallbacks
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.test.base.TestSource
import com.badoo.reaktive.utils.freeze

class TestSingle<T>(autoFreeze: Boolean = true) : TestSource<SingleObserver<T>>(), Single<T>, SingleCallbacks<T> {

    init {
        if (autoFreeze) {
            freeze()
        }
    }

    override fun onSuccess(value: T) {
        onEvent { it.onSuccess(value) }
    }
}
