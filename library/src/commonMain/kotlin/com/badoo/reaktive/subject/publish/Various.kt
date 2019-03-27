package com.badoo.reaktive.subject.publish

import com.badoo.reaktive.subject.DefaultSubject

fun <T> publishSubject(): PublishSubject<T> =
    object : DefaultSubject<T>(), PublishSubject<T> {
    }
