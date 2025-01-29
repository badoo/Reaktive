package com.badoo.reaktive.utils

internal actual fun Throwable.isFatal(): Boolean =
    (this is VirtualMachineError) ||
        (this is ThreadDeath) ||
        (this is LinkageError)
