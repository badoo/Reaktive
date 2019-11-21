package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.maybeUnsafe

fun <T> Maybe<T>.asRxJava3Source(): io.reactivex.rxjava3.core.MaybeSource<T> =
    io.reactivex.rxjava3.core.MaybeSource { observer ->
        subscribe(observer.asReaktive())
    }

fun <T> Maybe<T>.asRxJava3(): io.reactivex.rxjava3.core.Maybe<T> =
    object : io.reactivex.rxjava3.core.Maybe<T>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.MaybeObserver<in T>) {
            this@asRxJava3.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.rxjava3.core.MaybeSource<out T>.asReaktive(): Maybe<T> =
    maybeUnsafe { observer ->
        subscribe(observer.asRxJava3())
    }

fun <T> io.reactivex.rxjava3.core.MaybeObserver<in T>.asReaktive(): MaybeObserver<T> =
    object : MaybeObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava3())
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

fun <T> MaybeObserver<T>.asRxJava3(): io.reactivex.rxjava3.core.MaybeObserver<T> =
    object : io.reactivex.rxjava3.core.MaybeObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3.onSubscribe(disposable.asReaktive())
        }

        override fun onSuccess(value: T) {
            this@asRxJava3.onSuccess(value)
        }

        override fun onComplete() {
            this@asRxJava3.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava3.onError(error)
        }
    }
