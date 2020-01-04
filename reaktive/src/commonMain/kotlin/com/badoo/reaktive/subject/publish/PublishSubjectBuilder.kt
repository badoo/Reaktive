package com.badoo.reaktive.subject.publish

import com.badoo.reaktive.subject.DefaultSubject

@Suppress("FunctionName")
fun <T> PublishSubject(): PublishSubject<T> =
    object : DefaultSubject<T>(), PublishSubject<T> {
    }
