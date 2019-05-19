package com.badoo.reaktive.test.base

import com.badoo.reaktive.disposable.Disposable

fun TestObserver<*>.dispose() {
    disposables.forEach(Disposable::dispose)
}

val TestObserver<*>.isDisposed: Boolean get() = disposables.all(Disposable::isDisposed)