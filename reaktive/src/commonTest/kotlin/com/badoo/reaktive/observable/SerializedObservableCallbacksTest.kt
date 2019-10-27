package com.badoo.reaktive.observable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SerializedObservableCallbacksTest {

    @Test
    fun delegates_onNext_IF_queue_is_empty() {
        val values = ArrayList<Int?>()
        val emitter = callbacks(onNext = { values.add(it) })

        emitter.onNext(0)
        emitter.onNext(null)
        emitter.onNext(1)

        assertEquals(listOf<Int?>(0, null, 1), values)
    }

    @Test
    fun delegates_onComplete_IF_queue_is_empty() {
        var isCalled = false
        val emitter = callbacks(onComplete = { isCalled = true })

        emitter.onComplete()

        assertTrue(isCalled)
    }

    @Test
    fun delegates_onError_IF_queue_is_empty() {
        val error = Exception()
        lateinit var errorFromCallback: Throwable
        val emitter = callbacks(onError = { errorFromCallback = it })

        emitter.onError(error)

        assertSame(error, errorFromCallback)
    }

    @Test
    fun does_not_delegate_onNext_after_onComplete() {
        var isCalled = false
        val emitter = callbacks(onNext = { isCalled = true })

        emitter.onComplete()
        emitter.onNext(0)

        assertFalse(isCalled)
    }

    @Test
    fun does_not_delegate_onNext_after_onError() {
        var isCalled = false
        val emitter = callbacks(onNext = { isCalled = true })

        emitter.onError(Exception())
        emitter.onNext(0)

        assertFalse(isCalled)

    }

    @Test
    fun does_not_delegate_onComplete_after_onError() {
        var isCalled = false
        val emitter = callbacks(onComplete = { isCalled = true })

        emitter.onError(Exception())
        emitter.onComplete()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_delegate_onError_after_onComplete() {
        var isCalled = false
        val emitter = callbacks(onError = { isCalled = true })

        emitter.onComplete()
        emitter.onError(Exception())

        assertFalse(isCalled)
    }

    @Test
    fun delivers_all_onNext_from_queue() {
        lateinit var emitter: SerializedObservableCallbacks<Int?>
        val values = ArrayList<Int?>()

        emitter =
            callbacks(
                onNext = {
                    if (it == 0) {
                        emitter.onNext(null)
                        emitter.onNext(1)
                    }
                    values += it
                }
            )

        emitter.onNext(0)

        assertEquals(listOf<Int?>(0, null, 1), values)
    }

    @Test
    fun delivers_onComplete_after_queue_is_processed() {
        lateinit var emitter: SerializedObservableCallbacks<Int?>
        val events = ArrayList<Any?>()

        emitter =
            callbacks(
                onNext = {
                    emitter.onComplete()
                    events += it
                },
                onComplete = { events += "complete" }
            )

        emitter.onNext(0)

        assertEquals("complete", events.lastOrNull())
    }

    @Test
    fun delivers_onError_after_queue_is_processed() {
        lateinit var emitter: SerializedObservableCallbacks<Int?>
        val error = Exception()
        val events = ArrayList<Any?>()

        emitter =
            callbacks(
                onNext = {
                    emitter.onError(error)
                    events += it
                },
                onError = { events += it }
            )

        emitter.onNext(0)

        assertSame(error, events.lastOrNull())
    }

    @Test
    fun does_not_deliver_onNext_after_onComplete_queued() {
        lateinit var emitter: SerializedObservableCallbacks<Int?>
        var isCalled = false

        emitter =
            callbacks(
                onNext = {
                    if (it == 0) {
                        emitter.onComplete()
                        emitter.onNext(1)
                    } else {
                        isCalled = true
                    }
                }
            )

        emitter.onNext(0)

        assertFalse(isCalled)
    }

    @Test
    fun does_not_deliver_onNext_after_onError_queued() {
        lateinit var emitter: SerializedObservableCallbacks<Int?>
        var isCalled = false

        emitter =
            callbacks(
                onNext = {
                    if (it == 0) {
                        emitter.onError(Exception())
                        emitter.onNext(1)
                    } else {
                        isCalled = true
                    }
                }
            )

        emitter.onNext(0)

        assertFalse(isCalled)
    }

    @Test
    fun does_not_deliver_onComplete_after_onError_queued() {
        lateinit var emitter: SerializedObservableCallbacks<Int?>
        var isComplete = false

        emitter =
            callbacks(
                onNext = {
                    emitter.onError(Exception())
                    emitter.onComplete()
                },
                onComplete = { isComplete = true }
            )

        emitter.onNext(0)

        assertFalse(isComplete)
    }

    @Test
    fun does_not_deliver_onError_after_onComplete_queued() {
        lateinit var emitter: SerializedObservableCallbacks<Int?>
        var isError = false

        emitter =
            callbacks(
                onNext = {
                    emitter.onComplete()
                    emitter.onError(Exception())
                },
                onError = { isError = true }
            )

        emitter.onNext(0)

        assertFalse(isError)
    }

    private fun callbacks(
        onNext: (Int?) -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ): SerializedObservableCallbacks<Int?> =
        SerializedObservableCallbacks(
            object : ObservableCallbacks<Int?> {
                override fun onNext(value: Int?) {
                    onNext.invoke(value)
                }

                override fun onComplete() {
                    onComplete.invoke()
                }

                override fun onError(error: Throwable) {
                    onError.invoke(error)
                }
            }
        )
}
