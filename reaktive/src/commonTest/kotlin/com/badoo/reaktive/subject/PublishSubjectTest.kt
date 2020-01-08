package com.badoo.reaktive.subject

import com.badoo.reaktive.subject.publish.PublishSubject
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test

class PublishSubjectTest : SubjectGenericTests by SubjectGenericTests(PublishSubject()) {


    @Test
    fun does_not_emit_anything_to_a_new_subscriber() {
        val subject = PublishSubject<Int?>()
        subject.onNext(0)
        val observer = subject.test()

        observer.assertNoValues()
    }
}
