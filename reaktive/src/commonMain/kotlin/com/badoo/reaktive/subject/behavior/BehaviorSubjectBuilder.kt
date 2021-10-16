package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.DefaultSubject
import com.badoo.reaktive.utils.atomic.AtomicReference

/**
 * Creates a new instance of [BehaviorSubject].
 *
 * @param initialValue an initial value of the returned [BehaviorSubject]
 */
@Suppress("FunctionName")
fun <T> BehaviorSubject(initialValue: T): BehaviorSubject<T> =
    object : DefaultSubject<T>(), BehaviorSubject<T> {
        @Suppress("ObjectPropertyName")
        private val _value = AtomicReference(initialValue)
        override val value: T get() = _value.value

        override fun onAfterSubscribe(observer: ObservableObserver<T>) {
            super.onAfterSubscribe(observer)

            observer.onNext(value)
        }

        override fun onBeforeNext(value: T) {
            super.onBeforeNext(value)

            _value.value = value
        }
    }
