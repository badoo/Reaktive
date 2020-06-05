package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
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

class CompletableByEmitterTest {

    private val emitterRef = AtomicReference<CompletableEmitter?>(null)
    private val emitter: CompletableEmitter get() = requireNotNull(emitterRef.value)
    private val completable = createCompletable(emitterRef)
    private val observer = completable.test()

    private fun createCompletable(emitterReference: AtomicReference<CompletableEmitter?>): Completable =
        completable { emitterReference.value = it }

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        observer.assertSubscribed()
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
    fun onComplete_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onComplete()

        observer.assertNotComplete()
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

        val observer = completable { throw error }.test()

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
    fun assigned_disposable_is_disposed_WHEN_onComplete_is_signalled() {
        val events = atomicList<String>()
        completable.subscribe(observer(onComplete = { events += "onComplete" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onComplete()

        assertEquals(listOf("onComplete", "dispose"), events.value)
    }

    @Test
    fun assigned_disposable_is_disposed_WHEN_onError_is_signalled() {
        val events = atomicList<String>()
        completable.subscribe(observer(onError = { events += "onError" }))
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
    fun isDisposed_is_disposed_WHEN_onError_is_signalled() {
        emitter.onError(Throwable())

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun does_not_complete_recursively_WHEN_completing() {
        val isCompletedRecursively = AtomicBoolean()
        val isCompleted = AtomicBoolean()

        completable.subscribe(
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

        completable.subscribe(
            observer(
                onComplete = { isCompletedRecursively.value = true },
                onError = { emitter.onComplete() }
            )
        )

        emitter.onError(Exception())

        assertFalse(isCompletedRecursively.value)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_completing() {
        val isErrorRecursively = AtomicBoolean()

        completable.subscribe(
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

        completable.subscribe(
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
        completable.subscribe(
            object : CompletableObserver {
                init {
                    ensureNeverFrozen()
                }

                override fun onSubscribe(disposable: Disposable) {
                    disposable.freeze()
                }

                override fun onComplete() {
                }

                override fun onError(error: Throwable) {
                }
            }
        )
    }

    private fun observer(
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ): CompletableObserver =
        object : CompletableObserver {
            override fun onSubscribe(disposable: Disposable) {
            }

            override fun onComplete() {
                onComplete.invoke()
            }

            override fun onError(error: Throwable) {
                onError.invoke(error)
            }
        }
}
