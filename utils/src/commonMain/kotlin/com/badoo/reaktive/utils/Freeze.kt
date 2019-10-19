package com.badoo.reaktive.utils

expect fun <T> T.freeze(): T

expect fun <T: Any> T.ensureNeverFrozen(): T
<<<<<<< HEAD
=======

expect val Any?.isFrozen: Boolean
>>>>>>> 6e37dad3d71e0d11bc9b8015470548a5c10eecc6
