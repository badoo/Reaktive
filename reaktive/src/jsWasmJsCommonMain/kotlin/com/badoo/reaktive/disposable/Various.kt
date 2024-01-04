package com.badoo.reaktive.disposable

@JsName("disposableWithCallback")
@Suppress("FunctionName")
actual inline fun Disposable(crossinline onDispose: () -> Unit): Disposable =
    object : Disposable {
        override var isDisposed: Boolean = false
            private set

        override fun dispose() {
            if (!isDisposed) {
                isDisposed = true
                onDispose()
            }
        }
    }

@JsName("disposable")
@Suppress("FunctionName")
actual fun Disposable(): Disposable = SimpleDisposable()

private class SimpleDisposable : Disposable {
    override var isDisposed: Boolean = false
        private set

    override fun dispose() {
        isDisposed = true
    }
}
