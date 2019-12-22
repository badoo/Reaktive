package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse

interface MaybeToObservableTests : SourceTests {

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_succeeded()
}

@Ignore
class MaybeToObservableTestsImpl(
    transform: Maybe<Unit>.() -> Observable<*>
) : MaybeToObservableTests, SourceTests by SourceTestsImpl(TestMaybe<Nothing>(), { transform().test() }) {

    @Deprecated("Just to fix complilation issues")
    constructor() : this({ throw UnsupportedOperationException() })

    private val upstream = TestMaybe<Unit>()
    private val observer = upstream.transform().test()

    override fun completes_WHEN_upstream_is_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    override fun unsubscribes_from_upstream_WHEN_disposed() {
        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }

    override fun disposes_downstream_disposable_WHEN_upstream_succeeded() {
        upstream.onSuccess(Unit)

        observer.assertDisposed()
    }
}
