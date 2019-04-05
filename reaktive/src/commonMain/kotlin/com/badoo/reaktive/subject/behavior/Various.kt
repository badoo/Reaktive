package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.DefaultSubject
import com.badoo.reaktive.utils.synchronizedReadWriteProperty

fun <T> behaviorSubject(initialValue: T): BehaviorSubject<T> =
    object : DefaultSubject<T>(), BehaviorSubject<T> {
        override var value: T by synchronizedReadWriteProperty(initialValue)

        override fun onAfterSubscribe(observer: ObservableObserver<T>) {
            super.onAfterSubscribe(observer)

            observer.onNext(value)
        }

        override fun onBeforeNext(value: T) {
            super.onBeforeNext(value)

            this.value = value
        }
    }