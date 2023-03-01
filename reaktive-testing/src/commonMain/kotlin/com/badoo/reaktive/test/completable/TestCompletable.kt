package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.test.base.TestSource

class TestCompletable : TestSource<CompletableObserver>(), Completable, CompletableCallbacks {

    override fun onComplete() {
        onEvent { it.onComplete() }
    }
}
