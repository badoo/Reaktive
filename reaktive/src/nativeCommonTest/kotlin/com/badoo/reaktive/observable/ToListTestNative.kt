package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertFalse

class ToListTestNative {

    @Test
    fun collects_all_items_without_freezing() {
        val upstream = TestObservable<Int?>(autoFreeze = false)
        val observer = upstream.toList().test(autoFreeze = false)

        upstream.onNext(0, null, 1, null, 2)
        upstream.onComplete()

        observer.assertSuccess(listOf(0, null, 1, null, 2))
        assertFalse(observer.value.isFrozen)
    }
}