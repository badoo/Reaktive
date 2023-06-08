package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable

fun Disposable.asRxJava3Disposable(): io.reactivex.rxjava3.disposables.Disposable =
    object : io.reactivex.rxjava3.disposables.Disposable {
        override fun isDisposed(): Boolean = this@asRxJava3Disposable.isDisposed

        override fun dispose() {
            this@asRxJava3Disposable.dispose()
        }
    }

fun io.reactivex.rxjava3.disposables.Disposable.asReaktiveDisposable(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = this@asReaktiveDisposable.isDisposed

        override fun dispose() {
            this@asReaktiveDisposable.dispose()
        }
    }
