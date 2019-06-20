package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.completable.isComplete
import com.badoo.reaktive.test.completable.isError
import com.badoo.reaktive.test.completable.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompletableByEmitterTest {

    private lateinit var emitter: CompletableEmitter
    private val observer = completable { emitter = it }.test()

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        assertEquals(1, observer.disposables.size)
    }

    @Test
    fun completed_WHEN_onComplete_signalled() {
        emitter.onComplete()

        assertTrue(observer.isComplete)
    }

    @Test
    fun produces_same_error() {
        val error = Throwable()

        emitter.onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun second_onComplete_ignored_AFTER_first_onComplete_is_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onComplete()

        assertFalse(observer.isComplete)
    }

    @Test
    fun onComplete_ignored_AFTER_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onComplete()

        assertFalse(observer.isComplete)
    }

    @Test
    fun onError_ignored_AFTER_onComplete_is_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun second_onError_ignored_AFTER_first_onError_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun onComplete_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onComplete()

        assertFalse(observer.isComplete)
    }

    @Test
    fun disposable_disposed_AFTER_onComplete_is_signalled() {
        emitter.onComplete()

        assertTrue(observer.isDisposed)
    }

    @Test
    fun disposable_disposed_AFTER_onError_signalled() {
        emitter.onError(Throwable())

        assertTrue(observer.isDisposed)
    }

    @Test
    fun completed_with_error_WHEN_exception_during_subscribe() {
        val error = RuntimeException()

        completable { throw error }.subscribe(observer)

        assertTrue(observer.isError(error))
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
    fun isDisposed_is_disposed_WHEN_onError_is_signalled() {
        emitter.onError(Throwable())

        assertTrue(emitter.isDisposed)
    }
}