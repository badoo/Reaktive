package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsingTest :
    MaybeToMaybeTests by MaybeToMaybeTestsImpl(
        transform = { maybeUsing(resourceSupplier = {}, resourceCleanup = {}, sourceSupplier = { this }) }
    ) {

    private val disposables = Disposables()

    @Test
    fun acquires_new_resource_each_time_WHEN_eager_is_true_and_subscribed_multiple_times() {
        val downstream = maybeUsing(eager = true)

        repeat(3) { downstream.test() }

        assertEquals(3, disposables.size)
    }

    @Test
    fun acquires_new_resource_each_time_WHEN_eager_is_false_and_subscribed_multiple_times() {
        val downstream = maybeUsing(eager = false)

        repeat(3) { downstream.test() }

        assertEquals(3, disposables.size)
    }

    @Test
    fun disposes_resource_before_upstream_disposed_WHEN_eager_true_and_downstream_disposed() {
        var isResourceDisposedBeforeUpstreamDisposed = false
        val observer =
            maybeUsing(eager = true) { resource ->
                maybeUnsafe { observer ->
                    observer.onSubscribe(Disposable { isResourceDisposedBeforeUpstreamDisposed = resource.isDisposed })
                }
            }.test()

        observer.dispose()

        assertTrue(isResourceDisposedBeforeUpstreamDisposed)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_after_upstream_disposed_WHEN_eager_false_and_downstream_disposed() {
        var isResourceDisposedBeforeUpstreamDisposed = false

        val observer =
            maybeUsing(eager = false) { resource ->
                maybeUnsafe { observer ->
                    observer.onSubscribe(Disposable { isResourceDisposedBeforeUpstreamDisposed = resource.isDisposed })
                }
            }.test()

        observer.dispose()

        assertFalse(isResourceDisposedBeforeUpstreamDisposed)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_before_downstream_signalled_onComplete_WHEN_eager_true_and_upstream_completed() {
        var isResourceDisposedBeforeDownstreamOnComplete = false
        val upstream = TestMaybe<Int>()

        maybeUsing(eager = true, sourceSupplier = { upstream }).subscribe(
            object : DefaultMaybeObserver<Int> {
                override fun onComplete() {
                    isResourceDisposedBeforeDownstreamOnComplete = disposables.single().isDisposed
                }
            }
        )

        upstream.onComplete()

        assertTrue(isResourceDisposedBeforeDownstreamOnComplete)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_before_downstream_signalled_onSuccess_WHEN_eager_true_and_upstream_succeeded() {
        var isResourceDisposedBeforeDownstreamOnSuccess = false
        val upstream = TestMaybe<Int>()

        maybeUsing(eager = true, sourceSupplier = { upstream }).subscribe(
            object : DefaultMaybeObserver<Int> {
                override fun onSuccess(value: Int) {
                    isResourceDisposedBeforeDownstreamOnSuccess = disposables.single().isDisposed
                }
            }
        )

        upstream.onSuccess(0)

        assertTrue(isResourceDisposedBeforeDownstreamOnSuccess)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_before_downstream_signalled_onError_WHEN_eager_true_and_upstream_produced_error() {
        var isResourceDisposedBeforeDownstreamOnError = false
        val upstream = TestMaybe<Int>()

        maybeUsing(eager = true, sourceSupplier = { upstream }).subscribe(
            object : DefaultMaybeObserver<Int> {
                override fun onError(error: Throwable) {
                    isResourceDisposedBeforeDownstreamOnError = disposables.single().isDisposed
                }
            }
        )

        upstream.onError(Exception())

        assertTrue(isResourceDisposedBeforeDownstreamOnError)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_after_downstream_signalled_onComplete_WHEN_eager_false_and_upstream_completed() {
        var isResourceDisposedBeforeDownstreamOnComplete = false
        val upstream = TestMaybe<Int>()

        maybeUsing(eager = false, sourceSupplier = { upstream }).subscribe(
            object : DefaultMaybeObserver<Int> {
                override fun onComplete() {
                    isResourceDisposedBeforeDownstreamOnComplete = disposables.single().isDisposed
                }
            }
        )

        upstream.onComplete()

        assertFalse(isResourceDisposedBeforeDownstreamOnComplete)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_after_downstream_signalled_onSuccess_WHEN_eager_false_and_upstream_succeeded() {
        var isResourceDisposedBeforeDownstreamOnSuccess = false
        val upstream = TestMaybe<Int>()

        maybeUsing(eager = false, sourceSupplier = { upstream }).subscribe(
            object : DefaultMaybeObserver<Int> {
                override fun onSuccess(value: Int) {
                    isResourceDisposedBeforeDownstreamOnSuccess = disposables.single().isDisposed
                }
            }
        )

        upstream.onSuccess(0)

        assertFalse(isResourceDisposedBeforeDownstreamOnSuccess)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_after_downstream_signalled_onError_WHEN_eager_false_and_upstream_produced_error() {
        var isResourceDisposedBeforeDownstreamOnError = false
        val upstream = TestMaybe<Int>()

        maybeUsing(eager = false, sourceSupplier = { upstream }).subscribe(
            object : DefaultMaybeObserver<Int> {
                override fun onError(error: Throwable) {
                    isResourceDisposedBeforeDownstreamOnError = disposables.single().isDisposed
                }
            }
        )

        upstream.onError(Exception())

        assertFalse(isResourceDisposedBeforeDownstreamOnError)
        assertTrue(disposables.single().isDisposed)
    }

    private fun maybeUsing(
        eager: Boolean,
        sourceSupplier: (resource: Disposable) -> Maybe<Int> = { TestMaybe() },
    ): Maybe<Int> =
        maybeUsing(
            resourceSupplier = disposables::acquire,
            resourceCleanup = Disposable::dispose,
            eager = eager,
            sourceSupplier = sourceSupplier,
        )

    private class Disposables : MutableList<Disposable> by ArrayList() {
        fun acquire(): Disposable =
            Disposable().also {
                add(it)
            }
    }
}
