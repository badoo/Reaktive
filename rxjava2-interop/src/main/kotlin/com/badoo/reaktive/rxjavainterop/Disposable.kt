package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable

fun Disposable.asRxJava2(): io.reactivex.disposables.Disposable =
    object : io.reactivex.disposables.Disposable {
        override fun isDisposed(): Boolean = this@asRxJava2.isDisposed

        override fun dispose() {
            this@asRxJava2.dispose()
        }
    }

fun io.reactivex.disposables.Disposable.asReaktive(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = this@asReaktive.isDisposed

        override fun dispose() {
            this@asReaktive.dispose()
        }
    }
