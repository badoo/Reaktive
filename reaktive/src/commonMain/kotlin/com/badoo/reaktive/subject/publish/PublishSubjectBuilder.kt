package com.badoo.reaktive.subject.publish

import com.badoo.reaktive.subject.DefaultSubject

/**
 * Creates a new instance of [PublishSubject].
 */
@Suppress("FunctionName")
fun <T> PublishSubject(): PublishSubject<T> =
    object : DefaultSubject<T>(), PublishSubject<T> {
    }
