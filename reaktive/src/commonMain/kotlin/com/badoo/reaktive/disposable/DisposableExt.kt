package com.badoo.reaktive.disposable

fun <T : Disposable> T.setTo(disposableWrapper: DisposableWrapper): T {
    disposableWrapper.set(this)

    return this
}

fun <T : Disposable> T.addTo(compositeDisposable: CompositeDisposable): T {
    compositeDisposable.add(this)

    return this
}

internal inline fun Disposable.doIfNotDisposed(dispose: Boolean = false, block: () -> Unit) {
    if (!isDisposed) {
        try {
            block()
        } finally {
            if (dispose) {
                dispose()
            }
        }
    }
}
