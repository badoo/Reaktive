package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsingTest :
    CompletableToCompletableTests by CompletableToCompletableTestsImpl(
        transform = { completableUsing(resourceSupplier = {}, resourceCleanup = {}, sourceSupplier = { this }) }
    ) {

    private val disposables = Disposables()

    @Test
    fun acquires_new_resource_each_time_WHEN_eager_is_true_and_subscribed_multiple_times() {
        val downstream = completableUsing(eager = true)

        repeat(3) { downstream.test() }

        assertEquals(3, disposables.size)
    }

    @Test
    fun acquires_new_resource_each_time_WHEN_eager_is_false_and_subscribed_multiple_times() {
        val downstream = completableUsing(eager = false)

        repeat(3) { downstream.test() }

        assertEquals(3, disposables.size)
    }

    @Test
    fun disposes_resource_before_upstream_disposed_WHEN_eager_true_and_downstream_disposed() {
        var isResourceDisposedBeforeUpstreamDisposed by AtomicBoolean()
        val observer =
            completableUsing(eager = true) { resource ->
                completableUnsafe { observer ->
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
            completableUsing(eager = false) { resource ->
                completableUnsafe { observer ->
                    observer.onSubscribe(Disposable { isResourceDisposedBeforeUpstreamDisposed = resource.isDisposed })
                }
            }.test()

        observer.dispose()

        assertFalse(isResourceDisposedBeforeUpstreamDisposed)
        assertTrue(disposables.single().isDisposed)
    }

    @Test
    fun disposes_resource_before_downstream_signalled_onComplete_WHEN_eager_true_and_upstream_completed() {
        var isResourceDisposedBeforeDownstreamOnComplete by AtomicBoolean()
        val upstream = TestCompletable()

        completableUsing(eager = true, sourceSupplier = { upstream }).subscribe(
            object : DefaultCompletableObserver {
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
    fun disposes_resource_before_downstream_signalled_onError_WHEN_eager_true_and_upstream_produced_error() {
        var isResourceDisposedBeforeDownstreamOnError by AtomicBoolean()
        val upstream = TestCompletable()

        completableUsing(eager = true, sourceSupplier = { upstream }).subscribe(
            object : DefaultCompletableObserver {
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
        var isResourceDisposedBeforeDownstreamOnComplete by AtomicBoolean()
        val upstream = TestCompletable()

        completableUsing(eager = false, sourceSupplier = { upstream }).subscribe(
            object : DefaultCompletableObserver {
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
    fun disposes_resource_after_downstream_signalled_onError_WHEN_eager_false_and_upstream_produced_error() {
        var isResourceDisposedBeforeDownstreamOnError by AtomicBoolean()
        val upstream = TestCompletable()

        completableUsing(eager = false, sourceSupplier = { upstream }).subscribe(
            object : DefaultCompletableObserver {
                override fun onError(error: Throwable) {
                    isResourceDisposedBeforeDownstreamOnError = disposables.single().isDisposed
                }
            }
        )

        upstream.onError(Exception())

        assertFalse(isResourceDisposedBeforeDownstreamOnError)
        assertTrue(disposables.single().isDisposed)
    }

    private fun completableUsing(
        eager: Boolean,
        sourceSupplier: (resource: Disposable) -> Completable = { TestCompletable() },
    ): Completable =
        completableUsing(
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
