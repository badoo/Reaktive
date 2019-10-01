package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.test.base.TestSource
import com.badoo.reaktive.utils.freeze

class TestCompletable : TestSource<CompletableObserver>(), Completable, CompletableCallbacks {

    init {
        freeze()
    }

    override fun onComplete() {
        onEvent { it.onComplete() }
    }
}