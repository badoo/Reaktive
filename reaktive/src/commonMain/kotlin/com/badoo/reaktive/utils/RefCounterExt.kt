package com.badoo.reaktive.utils

internal inline fun <T> RefCounter.use(block: () -> T): T? =
    if (retain()) {
        try {
            block()
        } finally {
            release()
        }
    } else {
        null
    }
