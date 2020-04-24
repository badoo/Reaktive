package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable

fun Completable.asRxJava3CompletableSource(): io.reactivex.rxjava3.core.CompletableSource =
    io.reactivex.rxjava3.core.CompletableSource { observer ->
        subscribe(observer.asReaktiveCompletableObserver())
    }

@Deprecated(message = "Use asRxJava3CompletableSource", replaceWith = ReplaceWith("asRxJava3CompletableSource()"))
fun Completable.asRxJava3Source(): io.reactivex.rxjava3.core.CompletableSource = asRxJava3CompletableSource()

fun Completable.asRxJava3Completable(): io.reactivex.rxjava3.core.Completable =
    object : io.reactivex.rxjava3.core.Completable() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.CompletableObserver) {
            this@asRxJava3Completable.subscribe(observer.asReaktiveCompletableObserver())
        }
    }

@Deprecated(message = "Use asRxJava3Completable", replaceWith = ReplaceWith("asRxJava3Completable()"))
fun Completable.asRxJava3(): io.reactivex.rxjava3.core.Completable = asRxJava3Completable()

fun io.reactivex.rxjava3.core.CompletableSource.asReaktiveCompletable(): Completable =
    completableUnsafe { observer ->
        subscribe(observer.asRxJava3CompletableObserver())
    }

@Deprecated(message = "Use asReaktiveCompletable", replaceWith = ReplaceWith("asReaktiveCompletable()"))
fun io.reactivex.rxjava3.core.CompletableSource.asReaktive(): Completable = asReaktiveCompletable()

fun io.reactivex.rxjava3.core.CompletableObserver.asReaktiveCompletableObserver(): CompletableObserver =
    object : CompletableObserver {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveCompletableObserver.onSubscribe(disposable.asRxJava3Disposable())
        }

        override fun onComplete() {
            this@asReaktiveCompletableObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktiveCompletableObserver.onError(error)
        }
    }

@Deprecated(message = "Use asReaktiveCompletableObserver", replaceWith = ReplaceWith("asReaktiveCompletableObserver()"))
fun io.reactivex.rxjava3.core.CompletableObserver.asReaktive(): CompletableObserver = asReaktiveCompletableObserver()

fun CompletableObserver.asRxJava3CompletableObserver(): io.reactivex.rxjava3.core.CompletableObserver =
    object : io.reactivex.rxjava3.core.CompletableObserver {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3CompletableObserver.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onComplete() {
            this@asRxJava3CompletableObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava3CompletableObserver.onError(error)
        }
    }

@Deprecated(message = "Use asRxJava3CompletableObserver", replaceWith = ReplaceWith("asRxJava3CompletableObserver()"))
fun CompletableObserver.asRxJava3(): io.reactivex.rxjava3.core.CompletableObserver = asRxJava3CompletableObserver()
