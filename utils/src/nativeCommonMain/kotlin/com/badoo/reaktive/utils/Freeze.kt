package com.badoo.reaktive.utils

import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze

actual fun <T> T.freeze(): T = freeze()

actual fun Any.ensureNeverFrozen() {
    ensureNeverFrozen()
}