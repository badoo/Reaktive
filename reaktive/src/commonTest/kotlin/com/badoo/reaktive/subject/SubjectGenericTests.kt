package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.testutils.hasOnNext
import com.badoo.reaktive.testutils.isCompleted
import com.badoo.reaktive.testutils.isError
import com.badoo.reaktive.testutils.test
import com.badoo.reaktive.testutils.values
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

interface SubjectGenericTests {

    companion object {
        operator fun invoke(subject: Subject<Int?>): SubjectGenericTests = SubjectGenericTestsImpl(subject)
    }
}

private class SubjectGenericTestsImpl(
    private val subject: Subject<Int?>
) : SubjectGenericTests {

    @Test
    fun broadcasts_values_to_all_observers() {
        val observers = List(5) { subject.test() }
        subject.onNext(0)
        subject.onNext(null)
        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        observers.forEach {
            assertEquals(listOf(0, null, 1, null, 2), it.values)
        }
    }

    @Test
    fun does_not_emit_values_recursively() {
        var count = 0
        var success = false

        subject.subscribe(
            object : ObservableObserver<Int?> {
                override fun onSubscribe(disposable: Disposable) {
                }

                override fun onNext(value: Int?) {
                    count++
                    if (value == 0) {
                        subject.onNext(1)
                        success = count == 1
                    }
                }

                override fun onComplete() {
                }

                override fun onError(error: Throwable) {
                }
            }
        )
        subject.onNext(0)

        assertTrue(success)
    }

    @Test
    fun completes_all_observers_WHEN_completed() {
        val observers = List(5) { subject.test() }
        subject.onNext(0)
        subject.onComplete()

        observers.forEach {
            assertTrue(it.isCompleted)
        }
    }

    @Test
    fun delivers_error_to_all_observers_WHEN_error_produced() {
        val observers = List(5) { subject.test() }
        val error = Throwable()
        subject.onNext(0)
        subject.onError(error)

        observers.forEach {
            assertTrue(it.isError(error))
        }
    }

    @Test
    fun does_not_emit_values_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        subject.onNext(0)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun does_not_emit_values_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        subject.onNext(0)

        assertFalse(observer.hasOnNext)
    }

    @Test
    fun does_not_produce_completion_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        observer.reset()
        subject.onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun does_not_produce_completion_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        subject.onComplete()

        assertFalse(observer.isCompleted)
    }

    @Test
    fun does_not_produce_error_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        subject.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun does_not_produce_error_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        observer.reset()
        subject.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun does_not_emit_anything_WHEN_subscribed_after_completion() {
        subject.onNext(0)
        subject.onComplete()
        val observer = subject.test()

        assertFalse(observer.hasOnNext)
    }


    @Test
    fun does_not_emit_anything_WHEN_subscribed_after_error() {
        subject.onNext(0)
        subject.onError(Throwable())
        val observer = subject.test()

        assertFalse(observer.hasOnNext)
    }
}