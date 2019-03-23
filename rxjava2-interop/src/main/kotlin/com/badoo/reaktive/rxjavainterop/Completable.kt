package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.disposable.Disposable

fun Completable.toRxJava2Source(): io.reactivex.CompletableSource =
    io.reactivex.CompletableSource { observer ->
        subscribe(observer.toReaktive())
    }

fun Completable.toRxJava2(): io.reactivex.Completable =
    object : io.reactivex.Completable() {
        override fun subscribeActual(observer: io.reactivex.CompletableObserver) {
            this@toRxJava2.subscribe(observer.toReaktive())
        }
    }

fun <T> io.reactivex.CompletableSource.toReaktive(): Completable =
    completable { observer ->
        subscribe(observer.toRxJava2())
    }

fun io.reactivex.CompletableObserver.toReaktive(): CompletableObserver =
    object : CompletableObserver {
        override fun onSubscribe(disposable: Disposable) {
            this@toReaktive.onSubscribe(disposable.toRxJava2())
        }

        override fun onComplete() {
            this@toReaktive.onComplete()
        }

        override fun onError(error: Throwable) {
            this@toReaktive.onError(error)
        }
    }

fun CompletableObserver.toRxJava2(): io.reactivex.CompletableObserver =
    object : io.reactivex.CompletableObserver {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@toRxJava2.onSubscribe(disposable.toReaktive())
        }

        override fun onComplete() {
            this@toRxJava2.onComplete()
        }

        override fun onError(error: Throwable) {
            this@toRxJava2.onError(error)
        }
    }