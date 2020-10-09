package com.badoo.reaktive.utils

internal inline fun RefCounter.use(block: () -> Unit) {
    if (retain()) {
        try {
            block()
        } finally {
            release()
        }
    }
}
