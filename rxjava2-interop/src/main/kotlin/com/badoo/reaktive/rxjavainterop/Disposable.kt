package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable

fun Disposable.asRxJava2Disposable(): io.reactivex.disposables.Disposable =
    object : io.reactivex.disposables.Disposable {
        override fun isDisposed(): Boolean = this@asRxJava2Disposable.isDisposed

        override fun dispose() {
            this@asRxJava2Disposable.dispose()
        }
    }

@Deprecated(message = "Use asRxJava2Disposable", replaceWith = ReplaceWith("asRxJava2Disposable()"))
fun Disposable.asRxJava2(): io.reactivex.disposables.Disposable = asRxJava2Disposable()

fun io.reactivex.disposables.Disposable.asReaktiveDisposable(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = this@asReaktiveDisposable.isDisposed

        override fun dispose() {
            this@asReaktiveDisposable.dispose()
        }
    }

@Deprecated(message = "Use asReaktiveDisposable", replaceWith = ReplaceWith("asReaktiveDisposable()"))
fun io.reactivex.disposables.Disposable.asReaktive(): Disposable = asReaktiveDisposable()
