package com.badoo.reaktive.scheduler

import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.observable.toList
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.test.waitForOrFail
import com.badoo.reaktive.utils.NANOS_IN_SECOND
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized
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
        val result = ObjectReference<List<String>>(emptyList())
        val lock = Lock()
        val condition = lock.newCondition()

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
            .subscribe(
                onSuccess = {
                    lock.synchronized {
                        result.value = it
                        condition.signal()
                    }
                }
            )

        lock.synchronized {
            condition.waitForOrFail(timeoutNanos = 20L * NANOS_IN_SECOND, predicate = { result.value.isNotEmpty() })
        }

        val list = result.value
        for (i in 0 until schedulers.size) {
            for (j in 0 until innerCount) {
                assertTrue(list.contains("$i $j"))
            }
        }
    }
}
