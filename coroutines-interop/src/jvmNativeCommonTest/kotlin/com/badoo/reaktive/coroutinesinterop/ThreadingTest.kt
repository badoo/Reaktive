package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.single.blockingGet
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.subscribeOn
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadingTest {

    @Test
    fun threading_test() {
        val result =
            singleFromCoroutine { 1 }
                .subscribeOn(computationScheduler)
                .observeOn(ioScheduler)
                .flatMap { value ->
                    singleFromCoroutine { value + 2 }
                        .subscribeOn(computationScheduler)
                }
                .observeOn(ioScheduler)
                .blockingGet()

        assertEquals(3, result)
    }
}
