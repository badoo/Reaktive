package com.badoo.reaktive.utils

import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual fun <T> T.freeze(): T = freeze()

actual fun <T: Any> T.ensureNeverFrozen(): T {
    ensureNeverFrozen()

    return this
}

actual val Any?.isFrozen: Boolean get() = isFrozen
