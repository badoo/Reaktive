package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlin.test.Test
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
    fun does_not_complete_WHEN_completed()

    @Test
    fun does_not_complete_WHEN_error_produced()

    @Test
    fun does_not_produce_error_WHEN_completed()

    @Test
    fun does_not_produce_error_WHEN_error_produced()

    @Test
    fun does_not_emit_anything_WHEN_subscribed_after_completion()

    @Test
    fun does_not_emit_anything_WHEN_subscribed_after_error()

    @Test
    fun does_not_emit_values_to_unsubscribed_observers()

    @Test
    fun completes_WHEN_subscribed_after_completion()

    @Test
    fun produces_error_WHEN_subscribed_after_error()

    companion object {
        operator fun invoke(subject: Subject<Int?>, subscriberCount: Int = 5): SubjectGenericTests =
            SubjectGenericTestsImpl(subject, subscriberCount)
    }
}

private class SubjectGenericTestsImpl(
    private val subject: Subject<Int?>,
    private val subscriberCount: Int
) : SubjectGenericTests {

    override fun broadcasts_values_to_all_observers() {
        val observers = List(subscriberCount) { subject.test() }
        observers.forEach(TestObservableObserver<*>::reset)
        subject.onNext(0)
        subject.onNext(null)
        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        observers.forEach {
            it.assertValues(0, null, 1, null, 2)
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
                    count.addAndGet(1)
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
        val observers = List(subscriberCount) { subject.test() }
        subject.onNext(0)
        subject.onComplete()

        observers.forEach(TestObservableObserver<*>::assertComplete)
    }

    override fun delivers_error_to_all_observers_WHEN_error_produced() {
        val observers = List(subscriberCount) { subject.test() }
        val error = Throwable()
        subject.onNext(0)
        subject.onError(error)

        observers.forEach {
            it.assertError(error)
        }
    }

    override fun does_not_emit_values_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        observer.reset()
        subject.onNext(0)

        observer.assertNoValues()
    }

    override fun does_not_emit_values_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        observer.reset()
        subject.onNext(0)

        observer.assertNoValues()
    }

    override fun does_not_complete_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        observer.reset()
        subject.onComplete()

        observer.assertNotComplete()
    }

    override fun does_not_complete_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        subject.onComplete()

        observer.assertNotComplete()
    }

    override fun does_not_produce_error_WHEN_completed() {
        val observer = subject.test()
        subject.onComplete()
        subject.onError(Throwable())

        observer.assertNotError()
    }

    override fun does_not_produce_error_WHEN_error_produced() {
        val observer = subject.test()
        subject.onError(Throwable())
        observer.reset()
        subject.onError(Throwable())

        observer.assertNotError()
    }

    override fun does_not_emit_anything_WHEN_subscribed_after_completion() {
        subject.onNext(0)
        subject.onComplete()
        val observer = subject.test()

        observer.assertNoValues()
    }

    override fun does_not_emit_anything_WHEN_subscribed_after_error() {
        subject.onNext(0)
        subject.onError(Throwable())
        val observer = subject.test()

        observer.assertNoValues()
    }

    override fun does_not_emit_values_to_unsubscribed_observers() {
        val observer = subject.test()
        observer.dispose()
        subject.onNext(0)
    }

    override fun completes_WHEN_subscribed_after_completion() {
        subject.onComplete()
        val observer = subject.test()

        observer.assertComplete()
    }

    override fun produces_error_WHEN_subscribed_after_error() {
        val error = Throwable()
        subject.onError(error)
        val observer = subject.test()

        observer.assertError(error)
    }
}
