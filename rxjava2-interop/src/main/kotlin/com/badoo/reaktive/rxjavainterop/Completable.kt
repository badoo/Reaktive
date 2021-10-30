package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable

fun Completable.asRxJava2CompletableSource(): io.reactivex.CompletableSource =
    io.reactivex.CompletableSource { observer ->
        subscribe(observer.asReaktiveCompletableObserver())
    }

fun Completable.asRxJava2Completable(): io.reactivex.Completable =
    object : io.reactivex.Completable() {
        override fun subscribeActual(observer: io.reactivex.CompletableObserver) {
            this@asRxJava2Completable.subscribe(observer.asReaktiveCompletableObserver())
        }
    }

fun io.reactivex.CompletableSource.asReaktiveCompletable(): Completable =
    completableUnsafe { observer ->
        subscribe(observer.asRxJava2CompletableObserver())
    }

fun io.reactivex.CompletableObserver.asReaktiveCompletableObserver(): CompletableObserver =
    object : CompletableObserver {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveCompletableObserver.onSubscribe(disposable.asRxJava2Disposable())
        }

        override fun onComplete() {
            this@asReaktiveCompletableObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktiveCompletableObserver.onError(error)
        }
    }

fun CompletableObserver.asRxJava2CompletableObserver(): io.reactivex.CompletableObserver =
    object : io.reactivex.CompletableObserver {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2CompletableObserver.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onComplete() {
            this@asRxJava2CompletableObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava2CompletableObserver.onError(error)
        }
    }
