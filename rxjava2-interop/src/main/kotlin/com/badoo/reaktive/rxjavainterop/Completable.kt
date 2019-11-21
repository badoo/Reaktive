package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable

fun Completable.asRxJava2Source(): io.reactivex.CompletableSource =
    io.reactivex.CompletableSource { observer ->
        subscribe(observer.asReaktive())
    }

fun Completable.asRxJava2(): io.reactivex.Completable =
    object : io.reactivex.Completable() {
        override fun subscribeActual(observer: io.reactivex.CompletableObserver) {
            this@asRxJava2.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.CompletableSource.asReaktive(): Completable =
    completableUnsafe { observer ->
        subscribe(observer.asRxJava2())
    }

fun io.reactivex.CompletableObserver.asReaktive(): CompletableObserver =
    object : CompletableObserver {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava2())
        }

        override fun onComplete() {
            this@asReaktive.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktive.onError(error)
        }
    }

fun CompletableObserver.asRxJava2(): io.reactivex.CompletableObserver =
    object : io.reactivex.CompletableObserver {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2.onSubscribe(disposable.asReaktive())
        }

        override fun onComplete() {
            this@asRxJava2.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava2.onError(error)
        }
    }
