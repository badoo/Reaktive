package com.badoo.reaktive.subject.publish

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.AbstractSubject

/**
 * Creates a new instance of [PublishSubject].
 */
@Suppress("FunctionName")
fun <T> PublishSubject(): PublishSubject<T> =
    object : AbstractSubject<T, Nothing?>(), PublishSubject<T> {
        override fun subscribe(observer: ObservableObserver<T>) {
            val disposable = observer.onSubscribe() ?: return
            onSubscribe(observer, disposable, null)
        }
    }
