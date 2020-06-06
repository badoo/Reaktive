package com.badoo.reaktive.test.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.freeze
import kotlin.test.Test

class TestObservableObserverNativeTest {

    @Test
    fun collects_values_if_frozen() {
        val observer = TestObservableObserver<Int?>()
        observer.onSubscribe(Disposable())
        observer.freeze()

        observer.onNext(0)
        observer.onNext(null)
        observer.onNext(1)

        observer.assertValues(0, null, 1)
    }

    @Test
    fun collects_values_if_not_frozen_and_then_frozen() {
        val observer = TestObservableObserver<Int?>()
        observer.onSubscribe(Disposable())

        observer.onNext(0)
        observer.onNext(null)
        observer.freeze()
        observer.onNext(1)
        observer.onNext(null)
        observer.onNext(2)

        observer.assertValues(0, null, 1, null, 2)
    }
}
