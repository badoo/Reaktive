package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable

fun Completable.asRxJava2CompletableSource(): io.reactivex.CompletableSource =
    io.reactivex.CompletableSource { observer ->
        subscribe(observer.asReaktiveCompletableObserver())
    }

@Deprecated(message = "Use asRxJava2CompletableSource", replaceWith = ReplaceWith("asRxJava2CompletableSource()"))
fun Completable.asRxJava2Source(): io.reactivex.CompletableSource = asRxJava2CompletableSource()

fun Completable.asRxJava2Completable(): io.reactivex.Completable =
    object : io.reactivex.Completable() {
        override fun subscribeActual(observer: io.reactivex.CompletableObserver) {
            this@asRxJava2Completable.subscribe(observer.asReaktiveCompletableObserver())
        }
    }

@Deprecated(message = "Use asRxJava2Completable", replaceWith = ReplaceWith("asRxJava2Completable()"))
fun Completable.asRxJava2(): io.reactivex.Completable = asRxJava2Completable()

fun io.reactivex.CompletableSource.asReaktiveCompletable(): Completable =
    completableUnsafe { observer ->
        subscribe(observer.asRxJava2CompletableObserver())
    }

@Deprecated(message = "Use asReaktiveCompletable", replaceWith = ReplaceWith("asReaktiveCompletable()"))
fun io.reactivex.CompletableSource.asReaktive(): Completable = asReaktiveCompletable()

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

@Deprecated(message = "Use asReaktiveCompletableObserver", replaceWith = ReplaceWith("asReaktiveCompletableObserver()"))
fun io.reactivex.CompletableObserver.asReaktive(): CompletableObserver = asReaktiveCompletableObserver()

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

@Deprecated(message = "Use asRxJava2CompletableObserver", replaceWith = ReplaceWith("asRxJava2CompletableObserver()"))
fun CompletableObserver.asRxJava2(): io.reactivex.CompletableObserver = asRxJava2CompletableObserver()
