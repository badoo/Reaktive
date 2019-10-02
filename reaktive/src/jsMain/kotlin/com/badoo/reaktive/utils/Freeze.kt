package com.badoo.reaktive.utils

actual fun <T> T.freeze(): T = this

actual fun Any.ensureNeverFrozen() {
    // no-op
}