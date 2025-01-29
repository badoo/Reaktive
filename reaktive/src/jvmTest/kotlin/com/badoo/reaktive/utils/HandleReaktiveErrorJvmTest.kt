package com.badoo.reaktive.utils

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class HandleReaktiveErrorJvmTest {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun rethrows_WHEN_VirtualMachineError() {
        assertFailsWith<MyVirtualMachineError> {
            handleReaktiveError(MyVirtualMachineError())
        }
    }

    @Test
    fun rethrows_WHEN_ThreadDeath() {
        assertFailsWith<ThreadDeath> {
            handleReaktiveError(ThreadDeath())
        }
    }

    @Test
    fun rethrows_WHEN_LinkageError() {
        assertFailsWith<LinkageError> {
            handleReaktiveError(LinkageError())
        }
    }

    @Test
    fun calls_reaktiveUncaughtErrorHandler_WHEN_exception_and_onError_not_supplied() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        handleReaktiveError(error)

        assertEquals(error, caughtException.value)
    }

    @Test
    fun calls_onError_WHEN_exception_and_onError_supplied() {
        val error = Exception()
        var receivedError: Throwable? = null

        handleReaktiveError(error = error, onError = { receivedError = it })

        assertEquals(error, receivedError)
    }

    @Test
    fun rethrows_WHEN_onError_thrown_VirtualMachineError() {
        assertFailsWith<MyVirtualMachineError> {
            handleReaktiveError(error = Exception(), onError = { throw MyVirtualMachineError() })
        }
    }

    @Test
    fun rethrows_WHEN_onError_thrown_ThreadDeath() {
        assertFailsWith<ThreadDeath> {
            handleReaktiveError(error = Exception(), onError = { throw ThreadDeath() })
        }
    }

    @Test
    fun rethrows_WHEN_onError_thrown_LinkageError() {
        assertFailsWith<LinkageError> {
            handleReaktiveError(error = Exception(), onError = { throw LinkageError() })
        }
    }

    @Test
    fun calls_reaktiveUncaughtErrorHandler_WHEN_onError_thrown_exception() {
        val caughtException = mockUncaughtExceptionHandler()
        val error1 = Exception()
        val error2 = Exception()

        handleReaktiveError(error = error1, onError = { throw error2 })

        val error = caughtException.value
        assertIs<CompositeException>(error)
        assertEquals(error1, error.cause1)
        assertEquals(error2, error.cause2)
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_VirtualMachineError() {
        reaktiveUncaughtErrorHandler = { throw MyVirtualMachineError() }

        assertFailsWith<MyVirtualMachineError> {
            handleReaktiveError(error = Exception())
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_ThreadDeath() {
        reaktiveUncaughtErrorHandler = { throw ThreadDeath() }

        assertFailsWith<ThreadDeath> {
            handleReaktiveError(error = Exception())
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_LinkageError() {
        reaktiveUncaughtErrorHandler = { throw LinkageError() }

        assertFailsWith<LinkageError> {
            handleReaktiveError(error = Exception())
        }
    }

    @Test
    fun swallows_WHEN_reaktiveUncaughtErrorHandler_thrown_exception() {
        reaktiveUncaughtErrorHandler = { throw Exception() }

        handleReaktiveError(error = Exception())
    }
}
