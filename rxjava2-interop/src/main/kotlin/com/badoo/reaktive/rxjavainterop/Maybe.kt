package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.maybeUnsafe

fun <T> Maybe<T>.asRxJava2Source(): io.reactivex.MaybeSource<T> =
    io.reactivex.MaybeSource { observer ->
        subscribe(observer.asReaktive())
    }

fun <T> Maybe<T>.asRxJava2(): io.reactivex.Maybe<T> =
    object : io.reactivex.Maybe<T>() {
        override fun subscribeActual(observer: io.reactivex.MaybeObserver<in T>) {
            this@asRxJava2.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.MaybeSource<out T>.asReaktive(): Maybe<T> =
    maybeUnsafe { observer ->
        subscribe(observer.asRxJava2())
    }

fun <T> io.reactivex.MaybeObserver<in T>.asReaktive(): MaybeObserver<T> =
    object : MaybeObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava2())
        }

        override fun onSuccess(value: T) {
            this@asReaktive.onSuccess(value)
        }

        override fun onComplete() {
            this@asReaktive.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktive.onError(error)
        }
    }

fun <T> MaybeObserver<T>.asRxJava2(): io.reactivex.MaybeObserver<T> =
    object : io.reactivex.MaybeObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2.onSubscribe(disposable.asReaktive())
        }

        override fun onSuccess(value: T) {
            this@asRxJava2.onSuccess(value)
        }

        override fun onComplete() {
            this@asRxJava2.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava2.onError(error)
        }
    }
