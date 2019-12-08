package com.badoo.reaktive.completable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoOnBeforeErrorTest
    : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ doOnBeforeError {} }) {

    private val upstream = TestCompletable()

    @Test
    fun calls_action_before_failing() {
        val callOrder = SharedList<Pair<String, Throwable>>()
        val exception = Throwable()

        upstream
            .doOnBeforeError { error ->
                callOrder += "action" to error
            }
            .subscribe(
                object : DefaultCompletableObserver {
                    override fun onError(error: Throwable) {
                        callOrder += "onError" to error
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action" to exception, "onError" to exception), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_completed() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeError {
                isCalled.value = true
            }
            .test()

        upstream.onComplete()

        assertFalse(isCalled.value)
    }

    @Test
    fun produces_CompositeException_WHEN_exception_in_lambda() {
        val error1 = Exception()
        val error2 = Exception()

        val observer =
            upstream
                .doOnBeforeError { throw error2 }
                .test()

        upstream.onError(error1)

        val error: Throwable? = observer.error
        assertTrue(error is CompositeException)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }
}
