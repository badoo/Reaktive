package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test

class OnErrorCompleteTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ onErrorComplete() }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ onErrorComplete() }) {

    private val upstream = TestObservable<Int?>()
    private val observer = upstream.onErrorComplete().test()

    @Ignore
    @Test
    override fun produces_error_WHEN_upstream_produced_error() {
        // not applicable
    }

    @Test
    fun completes_WHEN_upstream_produced_error() {
        upstream.onError(Exception())

        observer.assertComplete()
    }
}
