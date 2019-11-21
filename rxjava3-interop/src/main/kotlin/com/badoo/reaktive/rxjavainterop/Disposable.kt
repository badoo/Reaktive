package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable

fun Disposable.asRxJava3(): io.reactivex.rxjava3.disposables.Disposable =
    object : io.reactivex.rxjava3.disposables.Disposable {
        override fun isDisposed(): Boolean = this@asRxJava3.isDisposed

        override fun dispose() {
            this@asRxJava3.dispose()
        }
    }

fun io.reactivex.rxjava3.disposables.Disposable.asReaktive(): Disposable =
    object : Disposable {
        override val isDisposed: Boolean get() = this@asReaktive.isDisposed

        override fun dispose() {
            this@asReaktive.dispose()
        }
    }
