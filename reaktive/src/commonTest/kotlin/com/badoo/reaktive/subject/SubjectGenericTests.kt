package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.isComplete
import com.badoo.reaktive.test.observable.isError
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.observable.values
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

interface SubjectGenericTests {

    @Test
    fun broadcasts_values_to_all_observers()

    @Test
    fun does_not_emit_values_recursively()

    @Test
    fun completes_all_observers_WHEN_completed()

    @Test
    fun delivers_error_to_all_observers_WHEN_error_produced()

    @Test
    fun does_not_emit_values_WHEN_completed()

    @Test
    fun does_not_emit_values_WHEN_error_produced()

    @Test
    fun does_not_produce_completion_WHEN_completed()

    @Test
    fun does_not_produce_completion_WHEN_error_produced()

    @Test
    fun does_not_produce_error_WHEN_completed()

    @Test
    fun does_not_produce_error_WHEN_error_produced()

    @Test
    fun does_not_emit_anything_WHEN_subscribed_after_completion()

    @Test
    fun does_not_emit_anything_WHEN_subscribed_after_error()

    companion object {
        operator fun invoke(subject: Subject<Int?>): SubjectGenericTests = SubjectGenericTestsImpl(subject)
    }
}

private class SubjectGenericTestsImpl(
    private val subject: Subject<Int?>
) : SubjectGenericTests {

    override fun broadcasts_values_to_all_observers() {
        val observers = List(5) { subject.test() }
        observers.forEach(TestObservableObserver<*>::reset)
        subject.onNext(0)
        subject.onNext(null)
        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        observers.forEach {
            assertEquals(listOf(0, null, 1, null, 2), it.values)
        }
    }

    override fun does_not_emit_values_recursively() {
        val count = AtomicInt()
        val success = AtomicBoolean()

        subject.subscribe(
            object : ObservableObserver<Int?> {
                override fun onSubscribe(disposable: Disposable) {
                }

                override fun onNext(value: Int?) {
                    count.incrementAndGet(1)
                    if (value == 0) {
                        subject.onNext(1)
                        success.value = count.value == 1
                    }
                }

                override fun onComplete() {
                }

                override fun onError(error: Throwable) {
                }
            }
        )
        count.value = 0
        subject.onNext(0)

        assertTrue(success.value)
    }

    override fun completes_all_observers_WHEN_completed() {
        val observers = List(5) { subject.test() }
        subject.onNext(0)
        subject.onComplete()

        observers.forEach {
            assertTrue(it.isComplete)
        }
    }

    override fun delivers_error_to_all_observers_WHEN_error_produced() {
        val observers = List(5) { subject.test() }
        val error = Throwable()
        subject.onNext(0)
        subject.onError(error)

        observers.forEach {
            assertTrue(it.isError(error))
        }
    }

    override fun does_not_emit_values_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        observer.reset()
        subject.onNext(0)

        assertFalse(observer.hasOnNext)
    }

    override fun does_not_emit_values_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        observer.reset()
        subject.onNext(0)

        assertFalse(observer.hasOnNext)
    }

    override fun does_not_produce_completion_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        observer.reset()
        subject.onComplete()

        assertFalse(observer.isComplete)
    }

    override fun does_not_produce_completion_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        subject.onComplete()

        assertFalse(observer.isComplete)
    }

    override fun does_not_produce_error_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        subject.onError(Throwable())

        assertFalse(observer.isError)
    }

    override fun does_not_produce_error_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        observer.reset()
        subject.onError(Throwable())

        assertFalse(observer.isError)
    }

    override fun does_not_emit_anything_WHEN_subscribed_after_completion() {
        subject.onNext(0)
        subject.onComplete()
        val observer = subject.test()

        assertFalse(observer.hasOnNext)
    }

    override fun does_not_emit_anything_WHEN_subscribed_after_error() {
        subject.onNext(0)
        subject.onError(Throwable())
        val observer = subject.test()

        assertFalse(observer.hasOnNext)
    }
}