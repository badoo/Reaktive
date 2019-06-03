package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.single.isError
import com.badoo.reaktive.test.single.isSuccess
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.single.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SingleByEmitterTest {

    private lateinit var emitter: SingleEmitter<Int?>
    private val observer = single<Int?> { emitter = it }.test()

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        assertEquals(1, observer.disposables.size)
    }

    @Test
    fun succeeds_with_the_non_null_value() {
        emitter.onSuccess(0)

        assertEquals(0, observer.value)
    }

    @Test
    fun succeeds_with_the_null_value() {
        emitter.onSuccess(null)

        assertEquals(null, observer.value)
    }

    @Test
    fun produces_same_error() {
        val error = Throwable()

        emitter.onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun second_onSuccess_ignored_AFTER_first_onSuccess_is_signalled() {
        emitter.onSuccess(0)
        observer.reset()
        emitter.onSuccess(1)

        assertFalse(observer.isSuccess)
    }

    @Test
    fun onSuccess_ignored_AFTER_onError_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onSuccess(1)

        assertFalse(observer.isSuccess)
    }

    @Test
    fun onError_ignored_AFTER_onSuccess_is_signalled() {
        emitter.onSuccess(0)
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
    fun onSuccess_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onSuccess(0)

        assertFalse(observer.isSuccess)
    }

    @Test
    fun disposable_disposed_AFTER_onSuccess_is_signalled() {
        emitter.onSuccess(0)

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

        single<Int> { throw error }.subscribe(observer)

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
    fun assigned_disposable_is_disposed_WHEN_onSuccess_is_signalled() {
        val disposable = disposable()
        emitter.setDisposable(disposable)

        emitter.onSuccess(0)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onError_is_signalled() {
        val disposable = disposable()
        emitter.setDisposable(disposable)

        emitter.onError(Throwable())

        assertTrue(disposable.isDisposed)
    }
}