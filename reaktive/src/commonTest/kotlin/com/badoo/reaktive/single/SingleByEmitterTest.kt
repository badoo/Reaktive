package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.single.assertNotSuccess
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
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

class SingleByEmitterTest {

    private val emitterRef = AtomicReference<SingleEmitter<Int?>?>(null)
    private val emitter: SingleEmitter<Int?> get() = requireNotNull(emitterRef.value)
    private val single = createSingle(emitterRef)
    private val observer = single.test()

    // To avoid freezing of the test class
    private fun createSingle(emitterReference: AtomicReference<SingleEmitter<Int?>?>): Single<Int?> =
        single { emitterReference.value = it }

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
    fun onSuccess_ignored_AFTER_onError_is_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onSuccess(1)

        observer.assertNotSuccess()
    }

    @Test
    fun onError_ignored_AFTER_onSuccess_is_signalled() {
        emitter.onSuccess(0)
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
    fun disposable_disposed_WHEN_onSuccess_is_signalled() {
        emitter.onSuccess(0)

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

        val observer = single<Int> { throw error }.test()

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
    fun assigned_disposable_is_disposed_WHEN_onSuccess_is_signalled() {
        val events = atomicList<String>()
        single.subscribe(observer(onSuccess = { events += "onSuccess" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onSuccess(0)

        assertEquals(listOf("onSuccess", "dispose"), events.value)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onError_is_signalled() {
        val events = atomicList<String>()
        single.subscribe(observer(onError = { events += "onError" }))
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

        single.subscribe(
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
    fun does_not_success_recursively_WHEN_producing_error() {
        val isSucceededRecursively = AtomicBoolean()

        single.subscribe(
            observer(
                onSuccess = { isSucceededRecursively.value = true },
                onError = { emitter.onSuccess(0) }
            )
        )

        emitter.onError(Exception())

        assertFalse(isSucceededRecursively.value)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_succeeding() {
        val isErrorRecursively = AtomicBoolean()

        single.subscribe(
            observer(
                onSuccess = { emitter.onError(Exception()) },
                onError = { isErrorRecursively.value = true }
            )
        )

        emitter.onSuccess(0)

        assertFalse(isErrorRecursively.value)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_producing_error() {
        val isErrorRecursively = AtomicBoolean()
        val hasError = AtomicBoolean()

        single.subscribe(
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
        single.subscribe(
            object : SingleObserver<Int?> {
                init {
                    ensureNeverFrozen()
                }

                override fun onSubscribe(disposable: Disposable) {
                    disposable.freeze()
                }

                override fun onSuccess(value: Int?) {
                }

                override fun onError(error: Throwable) {
                }
            }
        )
    }

    private fun observer(
        onSuccess: (Int?) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ): SingleObserver<Int?> =
        object : SingleObserver<Int?> {
            override fun onSubscribe(disposable: Disposable) {
            }

            override fun onSuccess(value: Int?) {
                onSuccess.invoke(value)
            }

            override fun onError(error: Throwable) {
                onError.invoke(error)
            }
        }
}
