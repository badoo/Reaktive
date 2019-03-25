package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable

fun Disposable.toRxJava2(): io.reactivex.disposables.Disposable =
    object : io.reactivex.disposables.Disposable {
        override fun isDisposed(): Boolean = this@toRxJava2.isDisposed

        override fun dispose() {
            this@toRxJava2.dispose()
        }
    }

fun io.reactivex.disposables.Disposable.toReaktive(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = this@toReaktive.isDisposed

        override fun dispose() {
            this@toReaktive.dispose()
        }
    }