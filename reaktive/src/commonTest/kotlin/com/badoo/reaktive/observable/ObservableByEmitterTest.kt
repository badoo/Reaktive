package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservableObserver.Event
import com.badoo.reaktive.test.observable.getOnErrorValue
import com.badoo.reaktive.test.observable.getOnNextEvent
import com.badoo.reaktive.test.observable.getOnNextValue
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.isCompleted
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.isOnCompleteEvent
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ObservableByEmitterTest {

    private lateinit var emitter: ObservableEmitter<Int>
    private val observer = observable<Int> { emitter = it }.test()

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        assertEquals(1, observer.disposables.size)
    }

    @Test
    fun emitted_same_values_and_completed_in_the_same_order() {
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onNext(3)
        emitter.onComplete()

        assertEquals(4, observer.events.size)
        assertEquals(1, observer.getOnNextEvent(0).value)
        assertEquals(2, observer.getOnNextEvent(1).value)
        assertEquals(3, observer.getOnNextEvent(2).value)
        assertTrue(observer.isOnCompleteEvent(3))
    }

    @Test
    fun emitted_same_values_and_completed_with_error_in_the_same_order() {
        val error = Throwable()

        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onNext(3)
        emitter.onError(error)

        assertEquals(4, observer.events.size)
        assertEquals(1, observer.getOnNextValue(0))
        assertEquals(2, observer.getOnNextValue(1))
        assertEquals(3, observer.getOnNextValue(2))
        assertSame(error, observer.getOnErrorValue(3))
    }

    @Test
    fun emitted_same_values_in_the_same_order_WHEN_disposed_after_producing_values() {
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onNext(3)
        observer.dispose()

        assertEquals(1, observer.getOnNextValue(0))
        assertEquals(2, observer.getOnNextValue(1))
        assertEquals(3, observer.getOnNextValue(2))
    }

    @Test
    fun completed_WHEN_onComplete_signalled() {
        emitter.onComplete()

        assertTrue(observer.isCompleted)
    }

    @Test
    fun completed_with_error_WHEN_onError_signalled() {
        val error = Throwable()

        emitter.onError(error)

        assertTrue(observer.isError(error))
    }

    @Test
    fun onNext_ignored_AFTER_onCompleted_signalled() {
        emitter.onNext(1)
        emitter.onComplete()
        emitter.onNext(2)

        assertTrue(observer.events.last() is Event.OnComplete)
    }

    @Test
    fun onNext_ignored_AFTER_onError_signalled() {
        emitter.onNext(1)
        emitter.onError(Throwable())
        emitter.onNext(2)

        assertTrue(observer.events.last() is Event.OnError)
    }

    @Test
    fun onComplete_ignored_AFTER_onError_signalled() {
        emitter.onNext(1)
        emitter.onError(Throwable())
        emitter.onComplete()

        assertTrue(observer.events.last() is Event.OnError)
    }

    @Test
    fun onError_ignored_AFTER_onCompleted_signalled() {
        emitter.onNext(1)
        emitter.onComplete()
        emitter.onError(Throwable())

        assertTrue(observer.events.last() is Event.OnComplete)
    }

    @Test
    fun second_onComplete_ignored_AFTER_first_onComplete_signalled() {
        emitter.onNext(1)
        emitter.onComplete()
        emitter.onComplete()

        assertTrue(observer.events.last() is Event.OnComplete)
        assertEquals(1, observer.events.count { it is Event.OnComplete })
    }

    @Test
    fun second_onError_ignored_AFTER_first_onError_signalled() {
        val error1 = Throwable()

        emitter.onNext(1)
        emitter.onError(error1)
        emitter.onError(Throwable())

        assertSame(error1, (observer.events.last() as Event.OnError).error)
        assertEquals(1, observer.events.count { it is Event.OnError })
    }

    @Test
    fun onNext_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onNext(1)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun disposable_disposed_AHTER_onComplete_signalled() {
        emitter.onComplete()

        assertTrue(observer.isDisposed)
    }

    @Test
    fun disposable_disposed_AHTER_onError_signalled() {
        emitter.onError(Throwable())

        assertTrue(observer.isDisposed)
    }

    @Test
    fun completed_with_error_WHEN_exception_during_subscribe() {
        val error = RuntimeException()

        observable<Int> { throw error }.subscribe(observer)

        assertTrue(observer.isError(error))
    }
}