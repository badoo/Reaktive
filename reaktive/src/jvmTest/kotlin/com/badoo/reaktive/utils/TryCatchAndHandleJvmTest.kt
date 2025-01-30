package com.badoo.reaktive.utils

import com.badoo.reaktive.base.tryCatchAndHandle
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TryCatchAndHandleJvmTest {

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun rethrows_WHEN_block_thrown_VirtualMachineError() {
        assertFailsWith<MyVirtualMachineError> {
            tryCatchAndHandle { throw MyVirtualMachineError() }
        }
    }

    @Test
    fun rethrows_WHEN_block_thrown_ThreadDeath() {
        assertFailsWith<ThreadDeath> {
            tryCatchAndHandle { throw ThreadDeath() }
        }
    }

    @Test
    fun rethrows_WHEN_block_thrown_LinkageError() {
        assertFailsWith<LinkageError> {
            tryCatchAndHandle { throw LinkageError() }
        }
    }

    @Test
    fun rethrows_WHEN_errorTransformer_thrown_VirtualMachineError() {
        assertFailsWith<MyVirtualMachineError> {
            tryCatchAndHandle(
                errorTransformer = { throw MyVirtualMachineError() },
                block = { throw Exception() },
            )
        }
    }

    @Test
    fun rethrows_WHEN_errorTransformer_thrown_ThreadDeath() {
        assertFailsWith<ThreadDeath> {
            tryCatchAndHandle(
                errorTransformer = { throw ThreadDeath() },
                block = { throw Exception() },
            )
        }
    }

    @Test
    fun rethrows_WHEN_errorTransformer_thrown_LinkageError() {
        assertFailsWith<LinkageError> {
            tryCatchAndHandle(
                errorTransformer = { throw LinkageError() },
                block = { throw Exception() },
            )
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_VirtualMachineError() {
        reaktiveUncaughtErrorHandler = { throw MyVirtualMachineError() }

        assertFailsWith<MyVirtualMachineError> {
            tryCatchAndHandle { throw Exception() }
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_ThreadDeath() {
        reaktiveUncaughtErrorHandler = { throw ThreadDeath() }

        assertFailsWith<ThreadDeath> {
            tryCatchAndHandle { throw Exception() }
        }
    }

    @Test
    fun rethrows_WHEN_reaktiveUncaughtErrorHandler_thrown_LinkageError() {
        reaktiveUncaughtErrorHandler = { throw LinkageError() }

        assertFailsWith<LinkageError> {
            tryCatchAndHandle { throw Exception() }
        }
    }
}
