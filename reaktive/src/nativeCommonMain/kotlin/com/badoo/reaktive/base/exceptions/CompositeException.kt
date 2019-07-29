package com.badoo.reaktive.base.exceptions

actual class CompositeException actual constructor(
    actual val cause1: Throwable,
    actual val cause2: Throwable
) : RuntimeException("Inner exception: $cause2")