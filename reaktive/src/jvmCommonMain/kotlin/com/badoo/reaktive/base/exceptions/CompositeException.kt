package com.badoo.reaktive.base.exceptions

import java.io.PrintWriter

actual class CompositeException actual constructor(
    actual val cause1: Throwable,
    actual val cause2: Throwable
) : RuntimeException() {

    override fun printStackTrace(writer: PrintWriter) {
        super.printStackTrace(writer)

        writer.print("Caused by 1: ")
        cause1.printStackTrace(writer)

        writer.print("Caused by 2: ")
        cause2.printStackTrace(writer)
    }
}
