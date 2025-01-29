package com.badoo.reaktive.utils

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.test.TestErrorCallback
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TryCatchJvmTest {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun rethrows_WHEN_block_thrown_VirtualMachineError() {
        val errorCallback = TestErrorCallback()

        assertFailsWith<MyVirtualMachineError> {
            errorCallback.tryCatch { throw MyVirtualMachineError() }
        }
    }

    @Test
    fun rethrows_WHEN_block_thrown_ThreadDeath() {
        val errorCallback = TestErrorCallback()

        assertFailsWith<ThreadDeath> {
            errorCallback.tryCatch { throw ThreadDeath() }
        }
    }

    @Test
    fun rethrows_WHEN_block_thrown_LinkageError() {
        val errorCallback = TestErrorCallback()

        assertFailsWith<LinkageError> {
            errorCallback.tryCatch { throw LinkageError() }
        }
    }

    @Test
    fun rethrows_WHEN_errorTransformer_thrown_VirtualMachineError() {
        val errorCallback = TestErrorCallback()

        assertFailsWith<MyVirtualMachineError> {
            errorCallback.tryCatch(
                errorTransformer = { throw MyVirtualMachineError() },
                block = { throw Exception() },
            )
        }
    }

    @Test
    fun rethrows_WHEN_errorTransformer_thrown_ThreadDeath() {
        val errorCallback = TestErrorCallback()

        assertFailsWith<ThreadDeath> {
            errorCallback.tryCatch(
                errorTransformer = { throw ThreadDeath() },
                block = { throw Exception() },
            )
        }
    }

    @Test
    fun rethrows_WHEN_errorTransformer_thrown_LinkageError() {
        val errorCallback = TestErrorCallback()

        assertFailsWith<LinkageError> {
            errorCallback.tryCatch(
                errorTransformer = { throw LinkageError() },
                block = { throw Exception() },
            )
        }
    }

    @Test
    fun rethrows_WHEN_ErrorCallback_thrown_VirtualMachineError() {
        val errorCallback = TestErrorCallback { throw MyVirtualMachineError() }

        assertFailsWith<MyVirtualMachineError> {
            errorCallback.tryCatch { throw Exception() }
        }
    }

    @Test
    fun rethrows_WHEN_ErrorCallback_thrown_ThreadDeath() {
        val errorCallback = TestErrorCallback { throw ThreadDeath() }

        assertFailsWith<ThreadDeath> {
            errorCallback.tryCatch { throw Exception() }
        }
    }

    @Test
    fun rethrows_WHEN_ErrorCallback_thrown_LinkageError() {
        val errorCallback = TestErrorCallback { throw LinkageError() }

        assertFailsWith<LinkageError> {
            errorCallback.tryCatch { throw Exception() }
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_VirtualMachineError() {
        reaktiveUncaughtErrorHandler = { throw MyVirtualMachineError() }
        val errorCallback = TestErrorCallback { throw Exception() }

        assertFailsWith<MyVirtualMachineError> {
            errorCallback.tryCatch { throw Exception() }
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_ThreadDeath() {
        reaktiveUncaughtErrorHandler = { throw ThreadDeath() }
        val errorCallback = TestErrorCallback { throw Exception() }

        assertFailsWith<ThreadDeath> {
            errorCallback.tryCatch { throw Exception() }
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_LinkageError() {
        reaktiveUncaughtErrorHandler = { throw LinkageError() }
        val errorCallback = TestErrorCallback { throw Exception() }

        assertFailsWith<LinkageError> {
            errorCallback.tryCatch { throw Exception() }
        }
    }
}
