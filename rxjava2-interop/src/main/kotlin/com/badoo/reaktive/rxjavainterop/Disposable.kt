package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable

fun Disposable.asRxJava2Disposable(): io.reactivex.disposables.Disposable =
    object : io.reactivex.disposables.Disposable {
        override fun isDisposed(): Boolean = this@asRxJava2Disposable.isDisposed

        override fun dispose() {
            this@asRxJava2Disposable.dispose()
        }
    }

fun io.reactivex.disposables.Disposable.asReaktiveDisposable(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = this@asReaktiveDisposable.isDisposed

        override fun dispose() {
            this@asReaktiveDisposable.dispose()
        }
    }
