package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.singleUnsafe

fun <T> Single<T>.asRxJava2Source(): io.reactivex.SingleSource<T> =
    io.reactivex.SingleSource { observer ->
        subscribe(observer.asReaktive())
    }

fun <T> Single<T>.asRxJava2(): io.reactivex.Single<T> =
    object : io.reactivex.Single<T>() {
        override fun subscribeActual(observer: io.reactivex.SingleObserver<in T>) {
            this@asRxJava2.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.SingleSource<out T>.asReaktive(): Single<T> =
    singleUnsafe { observer ->
        subscribe(observer.asRxJava2())
    }

fun <T> io.reactivex.SingleObserver<in T>.asReaktive(): SingleObserver<T> =
    object : SingleObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava2())
        }

        override fun onSuccess(value: T) {
            this@asReaktive.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asReaktive.onError(error)
        }
    }

fun <T> SingleObserver<T>.asRxJava2(): io.reactivex.SingleObserver<T> =
    object : io.reactivex.SingleObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2.onSubscribe(disposable.asReaktive())
        }

        override fun onSuccess(value: T) {
            this@asRxJava2.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asRxJava2.onError(error)
        }
    }
