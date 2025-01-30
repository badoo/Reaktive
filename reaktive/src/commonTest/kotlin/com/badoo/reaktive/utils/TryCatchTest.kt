package com.badoo.reaktive.utils

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.test.TestErrorCallback
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

class TryCatchTest {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun calls_ErrorCallback_WHEN_block_thrown_exception() {
        val errorCallback = TestErrorCallback()
        val error = Exception()

        errorCallback.tryCatch { throw error }

        assertSame(error, errorCallback.error)
    }

    @Test
    fun calls_ErrorCallback_WHEN_errorTransformer_thrown_exception() {
        val errorCallback = TestErrorCallback()
        val error1 = Exception()
        val error2 = Exception()

        errorCallback.tryCatch(
            errorTransformer = { throw error2 },
            block = { throw error1 },
        )

        val error = errorCallback.error
        assertIs<CompositeException>(error)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }

    @Test
    fun calls_reaktiveUncaughtErrorHandler_WHEN_ErrorCallback_thrown_exception() {
        val caughtException = mockUncaughtExceptionHandler()
        val error1 = Exception()
        val error2 = Exception()

        val errorCallback = TestErrorCallback { throw error2 }
        errorCallback.tryCatch { throw error1 }

        val error = caughtException.value
        assertIs<CompositeException>(error)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }

    @Test
    fun swallows_WHEN_reaktiveUncaughtErrorHandler_thrown_exception() {
        reaktiveUncaughtErrorHandler = { throw Exception() }
        val errorCallback = TestErrorCallback { throw Exception() }
        errorCallback.tryCatch { throw Exception() }
    }
}
