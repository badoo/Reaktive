package com.badoo.reaktive.utils

actual fun <T> T.freeze(): T = this

actual fun <T : Any> T.ensureNeverFrozen(): T = this
<<<<<<< HEAD
=======

actual val Any?.isFrozen: Boolean get() = false
>>>>>>> 6e37dad3d71e0d11bc9b8015470548a5c10eecc6
