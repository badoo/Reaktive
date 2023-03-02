package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsingTest :
    SingleToSingleTests by SingleToSingleTestsImpl(
        transform = { singleUsing(resourceSupplier = {}, resourceCleanup = {}, sourceSupplier = { this }) }
    ) {

    private val disposables = Disposables()

    @Test
    fun acquires_new_resource_each_time_WHEN_eager_is_true_and_subscribed_multiple_times() {
        val downstream = singleUsing(eager = true)

        repeat(3) { downstream.test() }

        assertEquals(3, disposables.size)
    }

    @Test
    fun acquires_new_resource_each_time_WHEN_eager_is_false_and_subscribed_multiple_times() {
        val downstream = singleUsing(eager = false)

        repeat(3) { downstream.test() }

        assertEquals(3, disposables.size)
    }

    @Test
    fun disposes_resource_before_upstream_disposed_WHEN_eager_true_and_downstream_disposed() {
        var isResourceDisposedBeforeUpstreamDisposed by AtomicBoolean()
        val observer =
            singleUsing(eager = true) { resource ->
                singleUnsafe { observer ->
                    observer.onSubscribe(Disposable { isResourceDisposedBeforeUpstreamDisposed = resource.isDisposed })
                }
            }.test()

        observer.dispose()

        assertTrue(isResourceDisposedBeforeUpstreamDisposed)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_after_upstream_disposed_WHEN_eager_false_and_downstream_disposed() {
        var isResourceDisposedBeforeUpstreamDisposed by AtomicBoolean()

        val observer =
            singleUsing(eager = false) { resource ->
                singleUnsafe { observer ->
                    observer.onSubscribe(Disposable { isResourceDisposedBeforeUpstreamDisposed = resource.isDisposed })
                }
            }.test()

        observer.dispose()

        assertFalse(isResourceDisposedBeforeUpstreamDisposed)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_before_downstream_signalled_onSuccess_WHEN_eager_true_and_upstream_succeeded() {
        var isResourceDisposedBeforeDownstreamOnSuccess by AtomicBoolean()
        val upstream = TestSingle<Int>()

        singleUsing(eager = true, sourceSupplier = { upstream }).subscribe(
            object : DefaultSingleObserver<Int> {
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
        var isResourceDisposedBeforeDownstreamOnError by AtomicBoolean()
        val upstream = TestSingle<Int>()

        singleUsing(eager = true, sourceSupplier = { upstream }).subscribe(
            object : DefaultSingleObserver<Int> {
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
    fun disposes_resource_after_downstream_signalled_onSuccess_WHEN_eager_false_and_upstream_succeeded() {
        var isResourceDisposedBeforeDownstreamOnSuccess by AtomicBoolean()
        val upstream = TestSingle<Int>()

        singleUsing(eager = false, sourceSupplier = { upstream }).subscribe(
            object : DefaultSingleObserver<Int> {
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
        var isResourceDisposedBeforeDownstreamOnError by AtomicBoolean()
        val upstream = TestSingle<Int>()

        singleUsing(eager = false, sourceSupplier = { upstream }).subscribe(
            object : DefaultSingleObserver<Int> {
                override fun onError(error: Throwable) {
                    isResourceDisposedBeforeDownstreamOnError = disposables.single().isDisposed
                }
            }
        )

        upstream.onError(Exception())

        assertFalse(isResourceDisposedBeforeDownstreamOnError)
        assertTrue(disposables.single().isDisposed)
    }

    private fun singleUsing(
        eager: Boolean,
        sourceSupplier: (resource: Disposable) -> Single<Int> = { TestSingle() },
    ): Single<Int> =
        singleUsing(
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
