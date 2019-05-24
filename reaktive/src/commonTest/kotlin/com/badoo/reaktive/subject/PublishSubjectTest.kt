package com.badoo.reaktive.subject

import com.badoo.reaktive.subject.publish.publishSubject
import com.badoo.reaktive.test.observable.hasOnNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse

class PublishSubjectTest : SubjectGenericTests by SubjectGenericTests(publishSubject()) {


    @Test
    fun does_not_emit_anything_to_a_new_subscriber() {
        val subject = publishSubject<Int?>()
        subject.onNext(0)
        val observer = subject.test()

        assertFalse(observer.hasOnNext)
    }
}