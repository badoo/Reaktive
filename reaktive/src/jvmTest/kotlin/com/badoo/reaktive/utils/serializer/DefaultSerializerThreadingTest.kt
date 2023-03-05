package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.test.doInBackground
import com.badoo.reaktive.utils.atomic.AtomicInt
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultSerializerThreadingTest {

    @Test
    fun delivers_all_values_concurrently() {
        fun test() {
            val values = HashSet<Int>()
            var counter = 0

            val serializer =
                serializer<Int> {
                    values += it
                    counter++
                    true
                }

            val value = AtomicInt(-1)

            race { _, _ ->
                serializer.accept(value.addAndGet(1))
            }

            assertEquals(THREAD_COUNT * ITERATION_COUNT, values.size)
            assertEquals(THREAD_COUNT * ITERATION_COUNT, counter)
        }

        repeat(200) {
            test()
        }
    }

    @Test
    fun onValue_never_called_after_returned_false() {
        fun test() {
            var isFinished = false
            var isCalledAfterFinished = false

            val serializer =
                serializer<Boolean> { value ->
                    if (isFinished) {
                        isCalledAfterFinished = true
                    } else if (!value) {
                        isFinished = true
                    }

                    value
                }

            race { threadIndex, iterationIndex ->
                serializer.accept((threadIndex != 0) || (iterationIndex != 1000))
            }

            assertFalse(isCalledAfterFinished)
        }

        repeat(200) {
            test()
        }
    }

    private fun race(block: (threadIndex: Int, iterationIndex: Int) -> Unit) {
        val startLatch = CountDownLatch(THREAD_COUNT)
        val finishLatch = CountDownLatch(THREAD_COUNT)
        var error: Throwable? = null

        repeat(THREAD_COUNT) { threadIndex ->
            doInBackground {
                startLatch.countDown()
                startLatch.await()

                try {
                    repeat(ITERATION_COUNT) { iterationIndex ->
                        block(threadIndex, iterationIndex)
                    }
                } catch (e: Throwable) {
                    error = e
                } finally {
                    finishLatch.countDown()
                }
            }
        }

        assertTrue(
            message = "Timeout waiting for the test to finish",
            actual = finishLatch.await(10L, TimeUnit.SECONDS),
        )

        error?.also { throw it }
    }

    private companion object {
        private const val THREAD_COUNT = 8
        private const val ITERATION_COUNT = 8000
    }
}
