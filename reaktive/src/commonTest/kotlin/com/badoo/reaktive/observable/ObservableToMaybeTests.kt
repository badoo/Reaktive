package com.badoo.reaktive.observable

import com.badoo.reaktive.base.SourceTests
import com.badoo.reaktive.base.SourceTestsImpl
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Ignore
import kotlin.test.Test

interface ObservableToMaybeTests : SourceTests {

    @Test
    fun completes_WHEN_upstream_is_completed()

    @Test
    fun disposes_downstream_disposable_WHEN_upstream_completed()
}

@Ignore
class ObservableToMaybeTestsImpl(
    transform: Observable<*>.() -> Maybe<*>
) : ObservableToMaybeTests, SourceTests by SourceTestsImpl(TestObservable<Nothing>(), { transform().test() }) {

    // See: https://youtrack.jetbrains.com/issue/KT-63132
    constructor() : this(transform = { error("Dummy") })

    private val upstream = TestObservable<Nothing>()
    private val observer = upstream.transform().test()

    override fun completes_WHEN_upstream_is_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    override fun disposes_downstream_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        observer.assertDisposed()
    }
}
