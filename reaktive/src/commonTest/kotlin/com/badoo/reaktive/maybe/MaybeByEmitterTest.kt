package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertNotComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MaybeByEmitterTest {

    private lateinit var emitter: MaybeEmitter<Int?>
    private val maybe = maybe { emitter = it }
    private val observer = maybe.test()

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        observer.assertSubscribed()
    }

    @Test
    fun succeeds_with_non_null_value() {
        emitter.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_null_value() {
        emitter.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun completed_WHEN_onComplete_signalled() {
        emitter.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_same_error() {
        val error = Throwable()

        emitter.onError(error)

        observer.assertError(error)
    }

    @Test
    fun second_onSuccess_ignored_AFTER_first_onSuccess_is_signalled() {
        emitter.onSuccess(0)
        observer.reset()
        emitter.onSuccess(1)

        observer.assertNotSuccess()
    }

    @Test
    fun onSuccess_ignored_AFTER_onComplete_is_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onSuccess(1)

        observer.assertNotSuccess()
    }

    @Test
    fun onSuccess_ignored_AFTER_onError_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onSuccess(1)

        observer.assertNotSuccess()
    }

    @Test
    fun onComplete_ignored_AFTER_onSuccess_signalled() {
        emitter.onSuccess(0)
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun second_onComplete_ignored_AFTER_first_onComplete_is_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun onComplete_ignored_AFTER_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun onError_ignored_AFTER_onSuccess_is_signalled() {
        emitter.onSuccess(0)
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun onError_ignored_AFTER_onComplete_is_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun second_onError_ignored_AFTER_first_onError_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun onSuccess_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onSuccess(0)

        observer.assertNotSuccess()
    }

    @Test
    fun onComplete_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun disposable_disposed_WHEN_onSuccess_is_signalled() {
        emitter.onSuccess(0)

        observer.assertDisposed()
    }

    @Test
    fun disposable_disposed_WHEN_onComplete_is_signalled() {
        emitter.onComplete()

        observer.assertDisposed()
    }

    @Test
    fun disposable_disposed_WHEN_onError_signalled() {
        emitter.onError(Throwable())

        observer.assertDisposed()
    }

    @Test
    fun completed_with_error_WHEN_exception_during_subscribe() {
        val error = RuntimeException()

        val observer = maybe<Int> { throw error }.test()

        observer.assertError(error)
    }

    @Test
    fun disposable_is_not_disposed_WHEN_assigned() {
        val disposable = Disposable()

        emitter.setDisposable(disposable)

        assertFalse(disposable.isDisposed)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_disposed() {
        val disposable = Disposable()
        emitter.setDisposable(disposable)

        observer.dispose()

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun reassigned_disposable_is_disposed_WHEN_disposed() {
        emitter.setDisposable(Disposable())
        observer.dispose()

        val disposable = Disposable()
        emitter.setDisposable(disposable)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun assigned_disposable_is_disposed_AFTER_onSuccess_is_signalled() {
        val events = ArrayList<String>()
        maybe.subscribe(observer(onSuccess = { events += "onSuccess" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onSuccess(0)

        assertEquals(listOf("onSuccess", "dispose"), events)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onComplete_is_signalled() {
        val events = ArrayList<String>()
        maybe.subscribe(observer(onComplete = { events += "onComplete" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onComplete()

        assertEquals(listOf("onComplete", "dispose"), events)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onError_is_signalled() {
        val events = ArrayList<String>()
        maybe.subscribe(observer(onError = { events += "onError" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onError(Throwable())

        assertEquals(listOf("onError", "dispose"), events)
    }

    @Test
    fun isDisposed_is_false_WHEN_created() {
        assertFalse(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_disposed() {
        observer.dispose()

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_onSuccess_is_signalled() {
        emitter.onSuccess(0)

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_disposed_WHEN_onError_is_signalled() {
        emitter.onError(Throwable())

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun does_not_success_recursively_WHEN_succeeding() {
        var isSucceededRecursively = false
        var isSucceeded = false

        maybe.subscribe(
            observer(
                onSuccess = {
                    if (!isSucceeded) {
                        isSucceeded = true
                        emitter.onSuccess(0)
                    } else {
                        isSucceededRecursively = true
                    }
                }
            )
        )

        emitter.onSuccess(0)

        assertFalse(isSucceededRecursively)
    }

    @Test
    fun does_not_success_recursively_WHEN_completing() {
        var isSucceededRecursively = false

        maybe.subscribe(
            observer(
                onSuccess = { isSucceededRecursively = true },
                onComplete = { emitter.onSuccess(0) }
            )
        )

        emitter.onComplete()

        assertFalse(isSucceededRecursively)
    }

    @Test
    fun does_not_success_recursively_WHEN_producing_error() {
        var isSucceededRecursively = false

        maybe.subscribe(
            observer(
                onSuccess = { isSucceededRecursively = true },
                onError = { emitter.onSuccess(0) }
            )
        )

        emitter.onError(Exception())

        assertFalse(isSucceededRecursively)
    }

    @Test
    fun does_not_complete_recursively_WHEN_succeeding() {
        var isCompletedRecursively = false

        maybe.subscribe(
            observer(
                onSuccess = { emitter.onComplete() },
                onComplete = { isCompletedRecursively = true }
            )
        )

        emitter.onSuccess(0)

        assertFalse(isCompletedRecursively)
    }

    @Test
    fun does_not_complete_recursively_WHEN_completing() {
        var isCompletedRecursively = false
        var isCompleted = false

        maybe.subscribe(
            observer(
                onComplete = {
                    if (!isCompleted) {
                        isCompleted = true
                        emitter.onComplete()
                    } else {
                        isCompletedRecursively = true
                    }
                }
            )
        )

        emitter.onComplete()

        assertFalse(isCompletedRecursively)
    }

    @Test
    fun does_not_complete_recursively_WHEN_producing_error() {
        var isCompletedRecursively = false

        maybe.subscribe(
            observer(
                onComplete = { isCompletedRecursively = true },
                onError = { emitter.onComplete() }
            )
        )

        emitter.onError(Exception())

        assertFalse(isCompletedRecursively)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_succeeding() {
        var isErrorRecursively = false

        maybe.subscribe(
            observer(
                onSuccess = { emitter.onError(Exception()) },
                onError = { isErrorRecursively = true }
            )
        )

        emitter.onSuccess(0)

        assertFalse(isErrorRecursively)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_completing() {
        var isErrorRecursively = false

        maybe.subscribe(
            observer(
                onComplete = { emitter.onError(Exception()) },
                onError = { isErrorRecursively = true }
            )
        )

        emitter.onComplete()

        assertFalse(isErrorRecursively)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_producing_error() {
        var isErrorRecursively = false
        var hasError = false

        maybe.subscribe(
            observer(
                onError = {
                    if (!hasError) {
                        hasError = true
                        emitter.onError(Exception())
                    } else {
                        isErrorRecursively = true
                    }
                }
            )
        )

        emitter.onError(Exception())

        assertFalse(isErrorRecursively)
    }

    private fun observer(
        onSuccess: (Int?) -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ): MaybeObserver<Int?> =
        object : MaybeObserver<Int?> {
            override fun onSubscribe(disposable: Disposable) {
            }

            override fun onSuccess(value: Int?) {
                onSuccess.invoke(value)
            }

            override fun onComplete() {
                onComplete.invoke()
            }

            override fun onError(error: Throwable) {
                onError.invoke(error)
            }
        }
}
