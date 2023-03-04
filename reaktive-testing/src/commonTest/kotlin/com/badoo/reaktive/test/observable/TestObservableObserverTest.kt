package com.badoo.reaktive.test.observable

import com.badoo.reaktive.disposable.Disposable
import kotlin.test.Test

class TestObservableObserverTest {

    @Test
    fun collects_values() {
        val observer = TestObservableObserver<Int?>()
        observer.onSubscribe(Disposable())

        observer.onNext(0)
        observer.onNext(null)
        observer.onNext(1)

        observer.assertValues(0, null, 1)
    }
}
