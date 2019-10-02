package com.badoo.reaktive.utils

expect fun <T> T.freeze(): T

expect fun Any.ensureNeverFrozen()