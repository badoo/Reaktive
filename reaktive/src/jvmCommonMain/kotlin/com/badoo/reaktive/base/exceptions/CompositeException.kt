package com.badoo.reaktive.base.exceptions

actual class CompositeException actual constructor(
    actual val cause1: Throwable,
    actual val cause2: Throwable
) : RuntimeException() {

    init {
        initCause(cause1)

        if (!cause1.hasCause(cause2)) {
            cause1.rootCause.initCause(cause2)
        }
    }

    private companion object {
        private fun Throwable.hasCause(throwable: Throwable): Boolean {
            var cause: Throwable? = this
            while (cause != null) {
                if (cause === throwable) {
                    return true
                }
                cause = cause.cause
            }

            return false
        }

        private val Throwable.rootCause: Throwable
            get() {
                var cause: Throwable = this
                while (true) {
                    cause = cause.cause ?: return cause
                }
            }
    }
}