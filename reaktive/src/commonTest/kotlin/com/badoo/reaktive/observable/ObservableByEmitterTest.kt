package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertTrue

class ObservableByEmitterTest {

    private lateinit var disposable: Disposable
    private lateinit var emitter: ObservableEmitter<Int>

    private val observer =
        mockk<ObservableObserver<Int>>(relaxed = true) {
            every { onSubscribe(any()) } answers { disposable = arg(0) }
        }

    init {
        observable<Int> { emitter = it }.subscribe(observer)
    }

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        verify { observer.onSubscribe(any()) }
    }

    @Test
    fun emitted_same_values_and_completed_in_the_same_order() {
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onNext(3)
        emitter.onComplete()

        verify(ordering = Ordering.ORDERED) {
            observer.onNext(1)
            observer.onNext(2)
            observer.onNext(3)
            observer.onComplete()
        }
    }

    @Test
    fun emitted_same_values_and_completed_with_error_in_the_same_order() {
        val error = mockk<Throwable>()

        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onNext(3)
        emitter.onError(error)

        verify(ordering = Ordering.ORDERED) {
            observer.onNext(1)
            observer.onNext(2)
            observer.onNext(3)
            observer.onError(refEq(error))
        }
    }

    @Test
    fun emitted_same_values_in_the_same_order_WHEN_disposed_after_producing_values() {
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onNext(3)
        disposable.dispose()

        verify(ordering = Ordering.ORDERED) {
            observer.onNext(1)
            observer.onNext(2)
            observer.onNext(3)
        }
    }

    @Test
    fun completed_WHEN_onComplete_signalled() {
        emitter.onComplete()

        verify { observer.onComplete() }
    }

    @Test
    fun completed_with_error_WHEN_onError_signalled() {
        val error = mockk<Throwable>()

        emitter.onError(error)

        verify { observer.onError(refEq(error)) }
    }

    @Test
    fun onNext_ignored_AFTER_onCompleted_signalled() {
        emitter.onNext(1)
        emitter.onComplete()
        emitter.onNext(2)

        verify(inverse = true) {
            observer.onNext(2)
        }
    }

    @Test
    fun onNext_ignored_AFTER_onError_signalled() {
        emitter.onNext(1)
        emitter.onError(mockk())
        emitter.onNext(2)

        verify(inverse = true) {
            observer.onNext(2)
        }
    }

    @Test
    fun onComplete_ignored_AFTER_onError_signalled() {
        emitter.onNext(1)
        emitter.onError(mockk())
        emitter.onComplete()

        verify(inverse = true) {
            observer.onComplete()
        }
    }

    @Test
    fun onError_ignored_AFTER_onCompleted_signalled() {
        emitter.onNext(1)
        emitter.onComplete()
        emitter.onError(mockk())

        verify(inverse = true) {
            observer.onError(any())
        }
    }

    @Test
    fun second_onComplete_ignored_AFTER_first_onComplete_signalled() {
        emitter.onNext(1)
        emitter.onComplete()
        emitter.onComplete()

        verify(exactly = 1) {
            observer.onComplete()
        }
    }

    @Test
    fun second_onError_ignored_AFTER_first_onError_signalled() {
        val error2 = mockk<Throwable>()

        emitter.onNext(1)
        emitter.onError(mockk())
        emitter.onError(error2)

        verify(inverse = true) {
            observer.onError(error2)
        }
    }

    @Test
    fun onNext_ignored_AFTER_dispose() {
        disposable.dispose()

        emitter.onNext(1)

        verify(inverse = true) {
            observer.onNext(any())
        }
    }

    @Test
    fun disposable_disposed_AHTER_onComplete_signalled() {
        emitter.onComplete()

        assertTrue(disposable.isDisposed)
    }


    @Test
    fun disposable_disposed_AHTER_onError_signalled() {
        emitter.onError(mockk())

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun completed_with_error_WHEN_exception_during_subscribe() {
        val error = RuntimeException()

        observable<Int> { throw error }.subscribe(observer)

        verify { observer.onError(refEq(error)) }
    }
}