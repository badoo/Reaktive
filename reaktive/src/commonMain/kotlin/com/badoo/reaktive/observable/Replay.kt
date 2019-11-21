package com.badoo.reaktive.observable

import com.badoo.reaktive.base.operator.publish
import com.badoo.reaktive.subject.DefaultSubject
import com.badoo.reaktive.utils.SharedList

fun <T> Observable<T>.replay(): ConnectableObservable<T> =
    publish {
        object : DefaultSubject<T>() {
            private val values = SharedList<T>()

            override fun onAfterSubscribe(observer: ObservableObserver<T>) {
                super.onAfterSubscribe(observer)

                values.forEach(observer::onNext)
            }

            override fun onBeforeNext(value: T) {
                super.onBeforeNext(value)

                values += value
            }
        }
    }
