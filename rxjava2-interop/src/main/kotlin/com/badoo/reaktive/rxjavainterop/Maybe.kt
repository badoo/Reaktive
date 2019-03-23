package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.maybe

fun <T> Maybe<T>.toRxJava2Source(): io.reactivex.MaybeSource<T> =
    io.reactivex.MaybeSource { observer ->
        subscribe(observer.toReaktive())
    }

fun <T> Maybe<T>.toRxJava2(): io.reactivex.Maybe<T> =
    object : io.reactivex.Maybe<T>() {
        override fun subscribeActual(observer: io.reactivex.MaybeObserver<in T>) {
            this@toRxJava2.subscribe(observer.toReaktive())
        }
    }

fun <T> io.reactivex.MaybeSource<out T>.toReaktive(): Maybe<T> =
    maybe { observer ->
        subscribe(observer.toRxJava2())
    }

fun <T> io.reactivex.MaybeObserver<in T>.toReaktive(): MaybeObserver<T> =
    object : MaybeObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@toReaktive.onSubscribe(disposable.toRxJava2())
        }

        override fun onSuccess(value: T) {
            this@toReaktive.onSuccess(value)
        }

        override fun onComplete() {
            this@toReaktive.onComplete()
        }

        override fun onError(error: Throwable) {
            this@toReaktive.onError(error)
        }
    }

fun <T> MaybeObserver<T>.toRxJava2(): io.reactivex.MaybeObserver<T> =
    object : io.reactivex.MaybeObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@toRxJava2.onSubscribe(disposable.toReaktive())
        }

        override fun onSuccess(value: T) {
            this@toRxJava2.onSuccess(value)
        }

        override fun onComplete() {
            this@toRxJava2.onComplete()
        }

        override fun onError(error: Throwable) {
            this@toRxJava2.onError(error)
        }
    }