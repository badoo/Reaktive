package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertNotComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MaybeByEmitterTest {

    private val emitterRef = AtomicReference<MaybeEmitter<Int?>?>(null)
    private val emitter: MaybeEmitter<Int?> get() = requireNotNull(emitterRef.value)
    private val observer = createMaybeAndSubscribe(emitterRef)

    private fun createMaybeAndSubscribe(emitterReference: AtomicReference<MaybeEmitter<Int?>?>): TestMaybeObserver<Int?> =
        maybe<Int?> { emitterReference.value = it }.test()

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        observer.assertSubscribed()
    }

    @Test
    fun succeeds_with_non_null_value() {
        emitter.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_null_value() {
        emitter.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun completed_WHEN_onComplete_signalled() {
        emitter.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_same_error() {
        val error = Throwable()

        emitter.onError(error)

        observer.assertError(error)
    }

    @Test
    fun second_onSuccess_ignored_AFTER_first_onSuccess_is_signalled() {
        emitter.onSuccess(0)
        observer.reset()
        emitter.onSuccess(1)

        observer.assertNotSuccess()
    }

    @Test
    fun onSuccess_ignored_AFTER_onComplete_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onSuccess(1)

        observer.assertNotSuccess()
    }

    @Test
    fun onSuccess_ignored_AFTER_onError_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onSuccess(1)

        observer.assertNotSuccess()
    }

    @Test
    fun onComplete_ignored_AFTER_onSuccess_signalled() {
        emitter.onSuccess(0)
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun second_onComplete_ignored_AFTER_first_onComplete_is_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun onComplete_ignored_AFTER_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun onError_ignored_AFTER_onSuccess_is_signalled() {
        emitter.onSuccess(0)
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun onError_ignored_AFTER_onComplete_is_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun second_onError_ignored_AFTER_first_onError_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun onSuccess_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onSuccess(0)

        observer.assertNotSuccess()
    }

    @Test
    fun onComplete_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun disposable_disposed_AFTER_onSuccess_is_signalled() {
        emitter.onSuccess(0)

        observer.assertDisposed()
    }

    @Test
    fun disposable_disposed_AFTER_onComplete_is_signalled() {
        emitter.onComplete()

        observer.assertDisposed()
    }

    @Test
    fun disposable_disposed_AFTER_onError_signalled() {
        emitter.onError(Throwable())

        observer.assertDisposed()
    }

    @Test
    fun completed_with_error_WHEN_exception_during_subscribe() {
        val error = RuntimeException()

        val observer = maybe<Int> { throw error }.test()

        observer.assertError(error)
    }

    @Test
    fun disposable_is_not_disposed_WHEN_assigned() {
        val disposable = disposable()

        emitter.setDisposable(disposable)

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_disposed() {
        val disposable = disposable()
        emitter.setDisposable(disposable)

        observer.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun reassigned_disposable_is_disposed_WHEN_disposed() {
        emitter.setDisposable(disposable())
        observer.dispose()

        val disposable = disposable()
        emitter.setDisposable(disposable)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onSuccess_is_signalled() {
        val disposable = disposable()
        emitter.setDisposable(disposable)

        emitter.onSuccess(0)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onComplete_is_signalled() {
        val disposable = disposable()
        emitter.setDisposable(disposable)

        emitter.onComplete()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onError_is_signalled() {
        val disposable = disposable()
        emitter.setDisposable(disposable)

        emitter.onError(Throwable())

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun isDisposed_is_false_WHEN_created() {
        assertFalse(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_disposed() {
        observer.dispose()

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_onSuccess_is_signalled() {
        emitter.onSuccess(0)

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_disposed_WHEN_onError_is_signalled() {
        emitter.onError(Throwable())

        assertTrue(emitter.isDisposed)
    }
}
