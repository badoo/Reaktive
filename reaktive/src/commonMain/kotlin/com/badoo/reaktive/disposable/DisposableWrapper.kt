package com.badoo.reaktive.disposable

/**
 * Thread-safe container of one [Disposable]
 */
@Deprecated(
    message = "Please use SerialDisposable",
    replaceWith = ReplaceWith("SerialDisposable"),
    level = DeprecationLevel.WARNING
)
open class DisposableWrapper : SerialDisposable()
