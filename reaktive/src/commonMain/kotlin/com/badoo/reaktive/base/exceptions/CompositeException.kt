package com.badoo.reaktive.base.exceptions

expect class CompositeException(
    cause1: Throwable,
    cause2: Throwable
) : RuntimeException {

    val cause1: Throwable
    val cause2: Throwable
}