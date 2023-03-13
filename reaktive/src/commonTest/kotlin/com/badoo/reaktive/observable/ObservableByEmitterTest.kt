package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObservableByEmitterTest {

    private lateinit var emitter: ObservableEmitter<Int?>
    private val observable = observable { emitter = it }
    private val observer = observable.test()

    @Test
    fun onSubscribe_called_WHEN_subscribe() {
        observer.assertSubscribed()
    }

    @Test
    fun emitted_same_values_and_completed_in_the_same_order() {
        emitter.onNext(null)
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onComplete()

        observer.assertValues(null, 1, 2)
        observer.assertComplete()
    }

    @Test
    fun emitted_same_values_and_completed_with_error_in_the_same_order() {
        val error = Throwable()

        emitter.onNext(null)
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onError(error)

        observer.assertValues(null, 1, 2)
        observer.assertError(error)
    }

    @Test
    fun emitted_same_values_in_the_same_order_WHEN_disposed_after_producing_values() {
        emitter.onNext(null)
        emitter.onNext(1)
        emitter.onNext(2)
        observer.dispose()

        observer.assertValues(null, 1, 2)
    }

    @Test
    fun completed_WHEN_onComplete_signalled() {
        emitter.onComplete()

        observer.assertComplete()
    }

    @Test
    fun completed_with_error_WHEN_onError_signalled() {
        val error = Throwable()

        emitter.onError(error)

        observer.assertError(error)
    }

    @Test
    fun onNext_ignored_AFTER_onCompleted_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onNext(2)

        observer.assertNoValues()
    }

    @Test
    fun onNext_ignored_AFTER_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onNext(2)

        observer.assertNoValues()
    }

    @Test
    fun onComplete_ignored_AFTER_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun onError_ignored_AFTER_onCompleted_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun second_onComplete_ignored_AFTER_first_onComplete_signalled() {
        emitter.onComplete()
        observer.reset()
        emitter.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun second_onError_ignored_AFTER_first_onError_signalled() {
        emitter.onError(Throwable())
        observer.reset()
        emitter.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun onNext_ignored_AFTER_dispose() {
        observer.dispose()

        emitter.onNext(1)

        observer.assertNoValues()
    }

    @Test
    fun disposable_disposed_WHEN_onComplete_signalled() {
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

        val observer = observable<Int> { throw error }.test()

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
    fun assigned_disposable_is_disposed_AFTER_onComplete_is_signalled() {
        val events = atomicList<String>()
        observable.subscribe(observer(onComplete = { events += "onComplete" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onComplete()

        assertEquals(listOf("onComplete", "dispose"), events.value)
    }

    @Test
    fun assigned_disposable_is_disposed_AFTER_onError_is_signalled() {
        val events = atomicList<String>()
        observable.subscribe(observer(onError = { events += "onError" }))
        emitter.setDisposable(Disposable { events += "dispose" })

        emitter.onError(Throwable())

        assertEquals(listOf("onError", "dispose"), events.value)
    }

    @Test
    fun isDisposed_is_false_WHEN_created() {
        assertFalse(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_false_WHEN_onNext_is_signalled() {
        observer.onNext(0)

        assertFalse(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_disposed() {
        observer.dispose()

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_true_WHEN_onComplete_is_signalled() {
        emitter.onComplete()

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun isDisposed_is_disposed_WHEN_onError_is_signalled() {
        emitter.onError(Throwable())

        assertTrue(emitter.isDisposed)
    }

    @Test
    fun does_not_emit_values_recursively_WHEN_completing() {
        var isEmittedRecursively = false

        observable.subscribe(
            observer(
                onNext = { isEmittedRecursively = true },
                onComplete = { emitter.onNext(0) }
            )
        )

        emitter.onComplete()

        assertFalse(isEmittedRecursively)
    }

    @Test
    fun does_not_emit_values_recursively_WHEN_producing_error() {
        var isEmittedRecursively = false

        observable.subscribe(
            observer(
                onNext = { isEmittedRecursively = true },
                onError = { emitter.onNext(0) }
            )
        )

        emitter.onError(Exception())

        assertFalse(isEmittedRecursively)
    }

    @Test
    fun does_not_complete_recursively_WHEN_completing() {
        var isCompletedRecursively = false
        var isCompleted = false

        observable.subscribe(
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

        observable.subscribe(
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

        observable.subscribe(
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

        observable.subscribe(
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
        onNext: (Int?) -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ): ObservableObserver<Int?> =
        object : ObservableObserver<Int?> {
            override fun onSubscribe(disposable: Disposable) {
            }

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
}
