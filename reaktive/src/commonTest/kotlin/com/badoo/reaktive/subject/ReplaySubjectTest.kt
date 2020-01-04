package com.badoo.reaktive.subject

import com.badoo.reaktive.subject.replay.ReplaySubject
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Ignore
import kotlin.test.Test

class ReplaySubjectTest : SubjectGenericTests by SubjectGenericTests(ReplaySubject()) {

    private val subject = ReplaySubject<Int?>(bufferSize = 5)

    @Test
    fun emits_buffered_values_to_a_first_subscriber() {
        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        val observer = subject.test()

        observer.assertValues(1, null, 2)
    }

    @Test
    fun emits_buffered_values_to_a_second_subscriber() {
        subject.onNext(1)
        subject.onNext(null)
        subject.test()
        subject.onNext(2)
        subject.onNext(null)

        val observer = subject.test()

        observer.assertValues(1, null, 2, null)
    }

    @Test
    fun emits_last_bufferSize_values() {
        subject.onNext(1)
        subject.onNext(2)
        subject.test()
        subject.onNext(3)
        subject.onNext(null)
        subject.onNext(4)
        subject.onNext(5)

        val observer = subject.test()

        observer.assertValues(2, 3, null, 4, 5)
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
