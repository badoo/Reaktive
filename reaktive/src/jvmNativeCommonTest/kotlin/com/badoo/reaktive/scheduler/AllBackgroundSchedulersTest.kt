package com.badoo.reaktive.scheduler

import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.observable.toList
import com.badoo.reaktive.single.blockingGet
import kotlin.test.Test
import kotlin.test.assertTrue

class AllBackgroundSchedulersTest {

    @Test
    fun foo() {
        val schedulers =
            listOf(
                computationScheduler,
                ioScheduler,
                singleScheduler,
                newThreadScheduler
            )

        val innerCount = 1000

        val result =
            List(schedulers.size) { it }
                .asObservable()
                .flatMap { index ->
                    val scheduler = schedulers[index % schedulers.size]

                    List(innerCount) { "$index $it" }
                        .asObservable()
                        .subscribeOn(scheduler)
                        .observeOn(scheduler)
                }
                .toList()
                .blockingGet()

        for (i in 0 until schedulers.size) {
            for (j in 0 until innerCount) {
                assertTrue(result.contains("$i $j"))
            }
        }
    }
}
