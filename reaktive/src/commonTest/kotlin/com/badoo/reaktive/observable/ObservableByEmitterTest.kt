package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.observable.getOnErrorValue
import com.badoo.reaktive.test.observable.getOnNextEvent
import com.badoo.reaktive.test.observable.getOnNextValue
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.isComplete
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.isOnCompleteEvent
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ObservableByEmitterTest {

    private lateinit var emitter: ObservableEmitter<Int?>
    private val observer = observable<Int?> { emitter = it }.test()

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        assertEquals(1, observer.disposables.size)
    }

    @Test
    fun emitted_same_values_and_completed_in_the_same_order() {
        emitter.onNext(null)
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onComplete()

        assertEquals(4, observer.events.size)
        assertEquals(null, observer.getOnNextEvent(0).value)
        assertEquals(1, observer.getOnNextEvent(1).value)
        assertEquals(2, observer.getOnNextEvent(2).value)
        assertTrue(observer.isOnCompleteEvent(3))
    }

    @Test
    fun emitted_same_values_and_completed_with_error_in_the_same_order() {
        val error = Throwable()

        emitter.onNext(null)
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onError(error)

        assertEquals(4, observer.events.size)
        assertEquals(null, observer.getOnNextValue(0))
        assertEquals(1, observer.getOnNextValue(1))
        assertEquals(2, observer.getOnNextValue(2))
        assertSame(error, observer.getOnErrorValue(3))
    }

    @Test
    fun emitted_same_values_in_the_same_order_WHEN_disposed_after_producing_values() {
        emitter.onNext(null)
        emitter.onNext(1)
        emitter.onNext(2)
        observer.dispose()

        assertEquals(null, observer.getOnNextValue(0))
        assertEquals(1, observer.getOnNextValue(1))
        assertEquals(2, observer.getOnNextValue(2))
    }

    @Test
    fun completed_WHEN_onComplete_signalled() {
        emitter.onComplete()

        assertTrue(observer.isComplete)
    }

    @Test
    fun completed_with_error_WHEN_onError_signalled() {
        val error = Throwable()

        emitter.onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun onNext_ignored_AFTER_onCompleted_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onNext(2)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun onNext_ignored_AFTER_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onNext(2)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun onComplete_ignored_AFTER_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onComplete()

        assertFalse(observer.isComplete)
    }

    @Test
    fun onError_ignored_AFTER_onCompleted_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun second_onComplete_ignored_AFTER_first_onComplete_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onComplete()

        assertFalse(observer.isComplete)
    }

    @Test
    fun second_onError_ignored_AFTER_first_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun onNext_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onNext(1)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun disposable_disposed_AFTER_onComplete_signalled() {
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

        observable<Int> { throw error }.subscribe(observer)

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
    fun isDisposed_is_false_WHEN_onNext_is_signalled() {
        observer.onNext(0)

        assertFalse(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_disposed() {
        observer.dispose()

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_onComplete_is_signalled() {
        emitter.onComplete()

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_disposed_WHEN_onError_is_signalled() {
        emitter.onError(Throwable())

        assertTrue(emitter.isDisposed)
    }
}