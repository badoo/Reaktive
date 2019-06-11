package com.badoo.reaktive.observable

import com.badoo.reaktive.test.completable.isComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AsCompletableTest : ObservableToCompletableTests by ObservableToCompletableTests({ asCompletable() }) {

    private val upstream = TestObservable<Int>()
    private val observer = upstream.asCompletable().test()

    @Test
    fun completes_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(observer.isComplete)
    }

    @Test
    fun does_not_complete_WHEN_upstream_emits_values() {
        upstream.onNext(0)
        upstream.onNext(1)
        upstream.onNext(2)

        assertFalse(observer.isComplete)
    }
}