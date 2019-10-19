package com.badoo.reaktive.utils

import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual fun <T> T.freeze(): T = freeze()

actual fun <T: Any> T.ensureNeverFrozen(): T {
    ensureNeverFrozen()

    return this
}
<<<<<<< HEAD
=======

actual val Any?.isFrozen: Boolean get() = isFrozen
>>>>>>> 6e37dad3d71e0d11bc9b8015470548a5c10eecc6
