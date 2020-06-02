package com.badoo.reaktive.disposable.scope

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.single.TestSingle
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DisposableScopeImplTest {

    private val scope = DisposableScopeImpl()

    @Test
    fun disposes_scoped_disposable_WHEN_disposed() {
        val disposable = Disposable()

        scope.run { disposable.scope() }
        scope.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun disposes_scoped_object_WHEN_disposed() {
        var isDisposed = false

        scope.run {
            Unit.scope { isDisposed = true }
        }
        scope.dispose()

        assertTrue(isDisposed)
    }

    @Test
    fun subscribes_to_Observable_WHEN_subscribeScoped_called() {
        val observable = TestObservable<Nothing>()

        scope.run { observable.subscribeScoped() }

        assertTrue(observable.hasSubscribers)
    }

    @Test
    fun completes_scoped_Observable_subscription_WHEN_disposed() {
        val observable = TestObservable<Nothing>()

        scope.run { observable.subscribeScoped() }
        scope.dispose()

        assertFalse(observable.hasSubscribers)
    }

    @Test
    fun subscribes_to_Single_WHEN_subscribeScoped_called() {
        val observable = TestSingle<Nothing>()

        scope.run { observable.subscribeScoped() }

        assertTrue(observable.hasSubscribers)
    }

    @Test
    fun completes_scoped_Single_subscription_WHEN_disposed() {
        val observable = TestSingle<Nothing>()

        scope.run { observable.subscribeScoped() }
        scope.dispose()

        assertFalse(observable.hasSubscribers)
    }

    @Test
    fun subscribes_to_Maybe_WHEN_subscribeScoped_called() {
        val observable = TestMaybe<Nothing>()

        scope.run { observable.subscribeScoped() }

        assertTrue(observable.hasSubscribers)
    }

    @Test
    fun completes_scoped_Maybe_subscription_WHEN_disposed() {
        val observable = TestMaybe<Nothing>()

        scope.run { observable.subscribeScoped() }
        scope.dispose()

        assertFalse(observable.hasSubscribers)
    }

    @Test
    fun subscribes_to_Completable_WHEN_subscribeScoped_called() {
        val observable = TestCompletable()

        scope.run { observable.subscribeScoped() }

        assertTrue(observable.hasSubscribers)
    }

    @Test
    fun completes_scoped_Completable_subscription_WHEN_disposed() {
        val observable = TestCompletable()

        scope.run { observable.subscribeScoped() }
        scope.dispose()

        assertFalse(observable.hasSubscribers)
    }
}
