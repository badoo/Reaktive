package com.badoo.reaktive.disposable

/**
 * Thread-safe container of one [Disposable]
 */
@Deprecated("Please use SerialDisposable", ReplaceWith("SerialDisposable"))
open class DisposableWrapper : SerialDisposable()
