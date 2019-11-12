package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable

fun Completable.asRxJava3Source(): io.reactivex.rxjava3.core.CompletableSource =
    io.reactivex.rxjava3.core.CompletableSource { observer ->
        subscribe(observer.asReaktive())
    }

fun Completable.asRxJava3(): io.reactivex.rxjava3.core.Completable =
    object : io.reactivex.rxjava3.core.Completable() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.CompletableObserver) {
            this@asRxJava3.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.rxjava3.core.CompletableSource.asReaktive(): Completable =
    completableUnsafe { observer ->
        subscribe(observer.asRxJava3())
    }

fun io.reactivex.rxjava3.core.CompletableObserver.asReaktive(): CompletableObserver =
    object : CompletableObserver {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava3())
        }

        override fun onComplete() {
            this@asReaktive.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktive.onError(error)
        }
    }

fun CompletableObserver.asRxJava3(): io.reactivex.rxjava3.core.CompletableObserver =
    object : io.reactivex.rxjava3.core.CompletableObserver {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3.onSubscribe(disposable.asReaktive())
        }

        override fun onComplete() {
            this@asRxJava3.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava3.onError(error)
        }
    }
