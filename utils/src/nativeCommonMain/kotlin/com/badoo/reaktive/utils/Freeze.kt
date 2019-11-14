@file:Suppress("NOTHING_TO_INLINE")

package com.badoo.reaktive.utils

import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual inline fun <T> T.freeze(): T = freeze()

actual inline fun <T : Any> T.ensureNeverFrozen(): T {
    ensureNeverFrozen()

    return this
}

actual inline val Any?.isFrozen: Boolean get() = isFrozen
