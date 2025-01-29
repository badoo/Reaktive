package com.badoo.reaktive.utils

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.tryCatchAndHandle
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

class TryCatchAndHandleTest {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun calls_reaktiveUncaughtErrorHandler_WHEN_block_thrown_exception() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        tryCatchAndHandle { throw error }

        assertSame(error, caughtException.value)
    }

    @Test
    fun calls_reaktiveUncaughtErrorHandler_WHEN_errorTransformer_thrown_exception() {
        val caughtException = mockUncaughtExceptionHandler()
        val error1 = Exception()
        val error2 = Exception()

        tryCatchAndHandle(
            errorTransformer = { throw error2 },
            block = { throw error1 },
        )

        val error = caughtException.value
        assertIs<CompositeException>(error)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }

    @Test
    fun swallows_WHEN_reaktiveUncaughtErrorHandler_thrown_exception() {
        reaktiveUncaughtErrorHandler = { throw Exception() }
        tryCatchAndHandle { throw Exception() }
    }
}
