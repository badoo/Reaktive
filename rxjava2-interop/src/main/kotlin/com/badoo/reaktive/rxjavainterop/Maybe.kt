package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.maybeUnsafe

fun <T : Any> Maybe<T>.asRxJava2Maybe(): io.reactivex.Maybe<T> =
    object : io.reactivex.Maybe<T>() {
        override fun subscribeActual(observer: io.reactivex.MaybeObserver<in T>) {
            this@asRxJava2Maybe.subscribe(observer.asReaktiveMaybeObserver())
        }
    }

fun <T : Any> io.reactivex.MaybeSource<out T>.asReaktiveMaybe(): Maybe<T> =
    maybeUnsafe { observer ->
        subscribe(observer.asRxJava2MaybeObserver())
    }

fun <T : Any> io.reactivex.MaybeObserver<in T>.asReaktiveMaybeObserver(): MaybeObserver<T> =
    object : MaybeObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveMaybeObserver.onSubscribe(disposable.asRxJava2Disposable())
        }

        override fun onSuccess(value: T) {
            this@asReaktiveMaybeObserver.onSuccess(value)
        }

        override fun onComplete() {
            this@asReaktiveMaybeObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktiveMaybeObserver.onError(error)
        }
    }

fun <T : Any> MaybeObserver<T>.asRxJava2MaybeObserver(): io.reactivex.MaybeObserver<T> =
    object : io.reactivex.MaybeObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2MaybeObserver.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onSuccess(value: T) {
            this@asRxJava2MaybeObserver.onSuccess(value)
        }

        override fun onComplete() {
            this@asRxJava2MaybeObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava2MaybeObserver.onError(error)
        }
    }
