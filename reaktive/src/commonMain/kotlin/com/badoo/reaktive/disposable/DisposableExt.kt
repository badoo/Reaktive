package com.badoo.reaktive.disposable

fun <T : Disposable> T.setTo(disposableWrapper: DisposableWrapper): T {
    disposableWrapper.set(this)

    return this
}

fun <T : Disposable> T.addTo(compositeDisposable: CompositeDisposable): T {
    compositeDisposable.add(this)

    return this
}

internal inline fun Disposable.wrap(
    crossinline onBeforeDispose: () -> Unit = {},
    crossinline onAfterDispose: () -> Unit = {}
): Disposable =
    object : Disposable by this {
        override fun dispose() {
            onBeforeDispose()
            this@wrap.dispose()
            onAfterDispose()
        }
    }