package com.badoo.reaktive.subject

import com.badoo.reaktive.subject.behavior.behaviorSubject
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class BehaviorSubjectTest : SubjectGenericTests by SubjectGenericTests(behaviorSubject(0)) {

    @Test
    fun emits_default_value_to_a_new_subscriber() {
        val subject = behaviorSubject(0)

        val observer = subject.test()

        observer.assertValue(0)
    }

    @Test
    fun emits_latest_value_to_a_new_subscriber() {
        val subject = behaviorSubject(0)
        subject.onNext(1)
        subject.onNext(2)

        val observer = subject.test()

        observer.assertValue(2)
    }
}
