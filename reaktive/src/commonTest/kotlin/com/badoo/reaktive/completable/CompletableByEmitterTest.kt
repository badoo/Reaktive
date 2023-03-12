package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompletableByEmitterTest {

    private lateinit var emitter: CompletableEmitter
    private val completable = completable { emitter = it }
    private val observer = completable.test()

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
        var isCompletedRecursively = false
        var isCompleted = false

        completable.subscribe(
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

        completable.subscribe(
            observer(
                onComplete = { isCompletedRecursively = true },
                onError = { emitter.onComplete() }
            )
        )

        emitter.onError(Exception())

        assertFalse(isCompletedRecursively)
    }

    @Test
    fun does_not_produce_error_recursively_WHEN_completing() {
        var isErrorRecursively = false

        completable.subscribe(
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

        completable.subscribe(
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
