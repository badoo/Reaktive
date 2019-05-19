package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.testutils.TestObservableObserver
import com.badoo.reaktive.testutils.hasOnNext
import com.badoo.reaktive.testutils.isCompleted
import com.badoo.reaktive.testutils.isError
import com.badoo.reaktive.testutils.test
import com.badoo.reaktive.testutils.values
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update
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
        val count = AtomicReference(0)
        val success = AtomicReference(false)

        subject.subscribe(
            object : ObservableObserver<Int?> {
                override fun onSubscribe(disposable: Disposable) {
                }

                override fun onNext(value: Int?) {
                    count.update { it + 1 }
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
            assertTrue(it.isCompleted)
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

        assertFalse(observer.isCompleted)
    }

    override fun does_not_produce_completion_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        subject.onComplete()

        assertFalse(observer.isCompleted)
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