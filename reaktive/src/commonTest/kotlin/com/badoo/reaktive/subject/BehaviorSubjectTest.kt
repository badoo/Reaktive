package com.badoo.reaktive.subject

import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BehaviorSubjectTest : SubjectGenericTests by SubjectGenericTests(BehaviorSubject(0)) {

    @Test
    fun emits_default_value_to_a_new_subscriber() {
        val subject = BehaviorSubject(0)

        val observer = subject.test()

        observer.assertValue(0)
    }

    @Test
    fun emits_latest_value_to_a_new_subscriber() {
        val subject = BehaviorSubject(0)
        subject.onNext(1)
        subject.onNext(2)

        val observer = subject.test()

        observer.assertValue(2)
    }

    @Test
    fun returns_default_value() {
        val subject = BehaviorSubject(0)

        val value = subject.value

        assertEquals(0, value)
    }

    @Test
    fun returns_latest_value() {
        val subject = BehaviorSubject(0)
        subject.onNext(1)
        subject.onNext(2)

        val value = subject.value

        assertEquals(2, value)
    }

    @Test
    fun emits_initial_value_synchronously_WHEN_subscribed_recursively() {
        val subject = BehaviorSubject(0)
        var emittedValues: List<Int>? = null

        subject.subscribe {
            if (it == 1) {
                emittedValues = subject.test().values
            }
        }

        subject.onNext(1)

        assertContentEquals(listOf(1), emittedValues)
    }

    @Test
    fun emits_all_queued_values_WHEN_subscribed_recursively() {
        val subject = BehaviorSubject<Int?>(0)
        var emittedValues: List<Int?>? = null

        var isSubscribedRecursively = false
        subject.subscribe {
            if ((it == 1) && !isSubscribedRecursively) {
                isSubscribedRecursively = true
                subject.onNext(2)
                subject.onNext(null)
                subject.onNext(1)
                emittedValues = subject.test().values
            }
        }

        subject.onNext(1)

        assertContentEquals(listOf(1, 2, null, 1), emittedValues)
    }
}
