package com.badoo.reaktive.subject

import com.badoo.reaktive.subject.unicast.UnicastSubject
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class UnicastSubjectTest : SubjectGenericTests by SubjectGenericTests(UnicastSubject(), 1) {

    private val subject = UnicastSubject<Int?>(bufferSize = 5)

    @Test
    fun emits_buffered_values_to_a_first_subscriber() {
        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        val observer = subject.test()

        observer.assertValues(1, null, 2)
    }

    @Test
    fun does_not_emit_any_values_to_a_second_subscriber() {
        subject.onNext(1)
        subject.onNext(null)
        subject.test()
        subject.onNext(2)
        subject.onNext(null)

        val observer = subject.test()

        observer.assertNoValues()
    }

    @Test
    fun produces_IllegalStateException_WHEN_second_subscriber() {
        subject.test()

        val observer = subject.test()

        observer.assertError { it is IllegalStateException }
    }

    @Test
    fun emits_last_bufferSize_values() {
        subject.onNext(1)
        subject.onNext(2)
        subject.onNext(3)
        subject.onNext(null)
        subject.onNext(4)
        subject.onNext(5)

        val observer = subject.test()

        observer.assertValues(2, 3, null, 4, 5)
    }

    @Test
    fun calls_onTerminate_WHEN_not_subscribed_and_onComplete_called() {
        val callCount = AtomicInt()
        val subject = UnicastSubject<Int?> { callCount.addAndGet(1) }

        subject.onComplete()

        assertEquals(1, callCount.value)
    }

    @Test
    fun calls_onTerminate_WHEN_subscribed_and_onComplete_called() {
        val callCount = AtomicInt()
        val subject = UnicastSubject<Int?> { callCount.addAndGet(1) }

        subject.test()
        subject.onComplete()

        assertEquals(1, callCount.value)
    }

    @Test
    fun calls_onTerminate_WHEN_not_subscribed_and_onError_called() {
        val callCount = AtomicInt()
        val subject = UnicastSubject<Int?> { callCount.addAndGet(1) }

        subject.onError(Throwable())

        assertEquals(1, callCount.value)
    }

    @Test
    fun calls_onTerminate_WHEN_subscribed_and_onError_called() {
        val callCount = AtomicInt()
        val subject = UnicastSubject<Int?> { callCount.addAndGet(1) }

        subject.test()
        subject.onError(Throwable())

        assertEquals(1, callCount.value)
    }

    @Test
    fun calls_onTerminate_WHEN_observer_unsubscribed() {
        val callCount = AtomicInt()
        val subject = UnicastSubject<Int?> { callCount.addAndGet(1) }
        val observer = subject.test()

        observer.dispose()

        assertEquals(1, callCount.value)
    }

    @Test
    fun status_completed_WHEN_observer_unsubscribed() {
        val subject = UnicastSubject<Int?>()
        val observer = subject.test()

        observer.dispose()

        assertEquals(Subject.Status.Completed, subject.status)
    }

    @Test
    @Ignore
    override fun does_not_emit_anything_WHEN_subscribed_after_completion() {
        // not applicable
    }

    @Test
    fun emits_buffered_values_WHEN_subscribed_after_completion() {
        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        subject.onComplete()
        val observer = subject.test()

        observer.assertValues(1, null, 2)
    }

    @Test
    @Ignore
    override fun does_not_emit_anything_WHEN_subscribed_after_error() {
        // not applicable
    }

    @Test
    fun emits_buffered_values_WHEN_subscribed_after_error() {
        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        subject.onError(Throwable())
        val observer = subject.test()

        observer.assertValues(1, null, 2)
    }
}
