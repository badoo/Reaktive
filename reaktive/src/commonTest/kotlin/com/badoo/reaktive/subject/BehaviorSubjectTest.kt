package com.badoo.reaktive.subject

import com.badoo.reaktive.subject.behavior.behaviorSubject
import com.badoo.reaktive.testutils.getOnNextValue
import com.badoo.reaktive.testutils.test
import com.badoo.reaktive.testutils.values
import kotlin.test.Test
import kotlin.test.assertEquals

class BehaviorSubjectTest : SubjectGenericTests by SubjectGenericTests(behaviorSubject(0)) {

    @Test
    fun emits_default_value_to_a_new_subscriber() {
        val subject = behaviorSubject(0)
        val observer = subject.test()

        assertEquals(listOf(0), observer.values)
    }

    @Test
    fun emits_latest_value_to_a_new_subscriber() {
        val subject = behaviorSubject(0)
        subject.onNext(1)
        val observer1 = subject.test()
        subject.onNext(2)
        val observer2 = subject.test()

        assertEquals(1, observer1.getOnNextValue(0))
        assertEquals(2, observer2.getOnNextValue(0))
    }
}