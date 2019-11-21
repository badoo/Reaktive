package com.badoo.reaktive.utils

expect inline fun <T> T.freeze(): T

expect inline fun <T : Any> T.ensureNeverFrozen(): T

// Uncomment when KT-31464 will be fixed.
expect /*inline*/ val Any?.isFrozen: Boolean
