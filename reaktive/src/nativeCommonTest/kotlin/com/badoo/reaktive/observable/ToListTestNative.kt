package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertTrue

class ToListTestNative {

    @Test
    fun collects_all_items_with_freezing() {
        val upstream = TestObservable<Int?>()
        val observer = upstream.toList().test()

        upstream.onNext(0, null, 1, null, 2)
        upstream.onComplete()

        observer.assertSuccess(listOf(0, null, 1, null, 2))
        assertTrue(observer.value.isFrozen)
    }

    @Test
    fun collects_all_items_with_freezing_by_upstream_in_the_middle() {
        val upstream = TestObservable<Int>(autoFreeze = false)
        val observer = upstream.toList().test(autoFreeze = false)

        upstream.onNext(0)
        upstream.freeze()
        upstream.onNext(1)
        upstream.onComplete()

        observer.assertSuccess(listOf(0, 1))
        assertTrue(observer.value.isFrozen)
    }
}
