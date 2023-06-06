package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class ToMapTest : ObservableToSingleTests by ObservableToSingleTestsImpl({ toMap { it } }) {

    @Test
    fun collects_all_items() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.toMap { "$it" }.test()

        upstream.onNext(0, null, 1, null, 2)
        upstream.onComplete()

        observer.assertSuccess(mapOf("0" to 0, "null" to null, "1" to 1, "2" to 2))
    }
}
