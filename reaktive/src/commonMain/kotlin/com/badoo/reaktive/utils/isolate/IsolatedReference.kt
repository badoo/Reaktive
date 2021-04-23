package com.badoo.reaktive.utils.isolate

internal expect class IsolatedReference<out T : Any>(value: T) : SharedReference<T>
