package com.badoo.reaktive.utils

expect fun <T> T.freeze(): T

expect fun <T: Any> T.ensureNeverFrozen(): T
