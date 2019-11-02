@file:Suppress("NOTHING_TO_INLINE")

package com.badoo.reaktive.utils

actual inline fun <T> T.freeze(): T = this

actual inline fun <T : Any> T.ensureNeverFrozen(): T = this

actual inline val Any?.isFrozen: Boolean get() = false
