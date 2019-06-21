package com.badoo.reaktive.observable

import com.badoo.reaktive.test.completable.isComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertFalse

class AsCompletableTest : ObservableToCompletableTests by ObservableToCompletableTests({ asCompletable() }) {

    private val upstream = TestObservable<Int>()
    private val observer = upstream.asCompletable().test()

    @Test
    fun does_not_complete_WHEN_upstream_emits_values() {
        upstream.onNext(0)
        upstream.onNext(1)
        upstream.onNext(2)

        assertFalse(observer.isComplete)
    }
}