package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class ToListTest : ObservableToSingleTests by ObservableToSingleTestsImpl({ toList() }) {

    @Test
    fun collects_all_items() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.toList().test()

        upstream.onNext(0, null, 1, null, 2)
        upstream.onComplete()

        observer.assertSuccess(listOf(0, null, 1, null, 2))
    }
}
