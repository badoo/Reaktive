package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable

fun Disposable.asRxJava3Disposable(): io.reactivex.rxjava3.disposables.Disposable =
    object : io.reactivex.rxjava3.disposables.Disposable {
        override fun isDisposed(): Boolean = this@asRxJava3Disposable.isDisposed

        override fun dispose() {
            this@asRxJava3Disposable.dispose()
        }
    }

@Deprecated(
    message = "Use asRxJava3Disposable",
    replaceWith = ReplaceWith("asRxJava3Disposable()"),
    level = DeprecationLevel.ERROR
)
fun Disposable.asRxJava3(): io.reactivex.rxjava3.disposables.Disposable = asRxJava3Disposable()

fun io.reactivex.rxjava3.disposables.Disposable.asReaktiveDisposable(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = this@asReaktiveDisposable.isDisposed

        override fun dispose() {
            this@asReaktiveDisposable.dispose()
        }
    }

@Deprecated(
    message = "Use asReaktiveDisposable",
    replaceWith = ReplaceWith("asReaktiveDisposable()"),
    level = DeprecationLevel.ERROR
)
fun io.reactivex.rxjava3.disposables.Disposable.asReaktive(): Disposable = asReaktiveDisposable()
