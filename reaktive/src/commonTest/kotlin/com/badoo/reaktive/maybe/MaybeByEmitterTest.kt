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
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import com.badoo.reaktive.utils.ensureNeverFrozen
import com.badoo.reaktive.utils.freeze
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MaybeByEmitterTest {

    private val emitterRef = AtomicReference<MaybeEmitter<Int?>?>(null)
    private val emitter: MaybeEmitter<Int?> get() = requireNotNull(emitterRef.value)
    private val maybe = createMaybe(emitterRef)
    private val observer = maybe.test()

    // To avoid freezing of the test class
    private fun createMaybe(emitterReference: AtomicReference<MaybeEmitter<Int?>?>): Maybe<Int?> =
        maybe { emitterReference.value = it }

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
        val events = atomicList<String>()
        maybe.subscribe(observer(onSuccess = { events += "onSuccess" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onSuccess(0)

        assertEquals(listOf("onSuccess", "dispose"), events.value)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onComplete_is_signalled() {
        val events = atomicList<String>()
        maybe.subscribe(observer(onComplete = { events += "onComplete" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onComplete()

        assertEquals(listOf("onComplete", "dispose"), events.value)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onError_is_signalled() {
        val events = atomicList<String>()
        maybe.subscribe(observer(onError = { events += "onError" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onError(Throwable())

        assertEquals(listOf("onError", "dispose"), events.value)
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
        val isSucceededRecursively = AtomicBoolean()
        val isSucceeded = AtomicBoolean()

        maybe.subscribe(
            observer(
                onSuccess = {
                    if (!isSucceeded.value) {
                        isSucceeded.value = true
                        emitter.onSuccess(0)
                    } else {
                        isSucceededRecursively.value = true
                    }
                }
            )
        )

        emitter.onSuccess(0)

        assertFalse(isSucceededRecursively.value)
    }

    @Test
    fun does_not_success_recursively_WHEN_completing() {
        val isSucceededRecursively = AtomicBoolean()

        maybe.subscribe(
            observer(
                onSuccess = { isSucceededRecursively.value = true },
                onComplete = { emitter.onSuccess(0) }
            )
        )

        emitter.onComplete()

        assertFalse(isSucceededRecursively.value)
    }

    @Test
    fun does_not_success_recursively_WHEN_producing_error() {
        val isSucceededRecursively = AtomicBoolean()

        maybe.subscribe(
            observer(
                onSuccess = { isSucceededRecursively.value = true },
                onError = { emitter.onSuccess(0) }
            )
        )

        emitter.onError(Exception())

        assertFalse(isSucceededRecursively.value)
    }

    @Test
    fun does_not_complete_recursively_WHEN_succeeding() {
        val isCompletedRecursively = AtomicBoolean()

        maybe.subscribe(
            observer(
                onSuccess = { emitter.onComplete() },
                onComplete = { isCompletedRecursively.value = true }
            )
        )

        emitter.onSuccess(0)

        assertFalse(isCompletedRecursively.value)
    }

    @Test
    fun does_not_complete_recursively_WHEN_completing() {
        val isCompletedRecursively = AtomicBoolean()
        val isCompleted = AtomicBoolean()

        maybe.subscribe(
            observer(
                onComplete = {
                    if (!isCompleted.value) {
                        isCompleted.value = true
                        emitter.onComplete()
                    } else {
                        isCompletedRecursively.value = true
                    }
                }
            )
        )

        emitter.onComplete()

        assertFalse(isCompletedRecursively.value)
    }

    @Test
    fun does_not_complete_recursively_WHEN_producing_error() {
        val isCompletedRecursively = AtomicBoolean()

        maybe.subscribe(
            observer(
                onComplete = { isCompletedRecursively.value = true },
                onError = { emitter.onComplete() }
            )
        )

        emitter.onError(Exception())

        assertFalse(isCompletedRecursively.value)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_succeeding() {
        val isErrorRecursively = AtomicBoolean()

        maybe.subscribe(
            observer(
                onSuccess = { emitter.onError(Exception()) },
                onError = { isErrorRecursively.value = true }
            )
        )

        emitter.onSuccess(0)

        assertFalse(isErrorRecursively.value)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_completing() {
        val isErrorRecursively = AtomicBoolean()

        maybe.subscribe(
            observer(
                onComplete = { emitter.onError(Exception()) },
                onError = { isErrorRecursively.value = true }
            )
        )

        emitter.onComplete()

        assertFalse(isErrorRecursively.value)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_producing_error() {
        val isErrorRecursively = AtomicBoolean()
        val hasError = AtomicBoolean()

        maybe.subscribe(
            observer(
                onError = {
                    if (!hasError.value) {
                        hasError.value = true
                        emitter.onError(Exception())
                    } else {
                        isErrorRecursively.value = true
                    }
                }
            )
        )

        emitter.onError(Exception())

        assertFalse(isErrorRecursively.value)
    }

    @Test
    fun does_not_freeze_observer_WHEN_disposable_is_frozen() {
        maybe.subscribe(
            object : MaybeObserver<Int?> {
                init {
                    ensureNeverFrozen()
                }

                override fun onSubscribe(disposable: Disposable) {
                    disposable.freeze()
                }

                override fun onSuccess(value: Int?) {
                }

                override fun onComplete() {
                }

                override fun onError(error: Throwable) {
                }
            }
        )
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
