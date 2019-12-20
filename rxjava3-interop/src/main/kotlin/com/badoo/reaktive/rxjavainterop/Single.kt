package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.singleUnsafe

fun <T> Single<T>.asRxJava3Source(): io.reactivex.rxjava3.core.SingleSource<T> =
    io.reactivex.rxjava3.core.SingleSource { observer ->
        subscribe(observer.asReaktive())
    }

fun <T> Single<T>.asRxJava3(): io.reactivex.rxjava3.core.Single<T> =
    object : io.reactivex.rxjava3.core.Single<T>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.SingleObserver<in T>) {
            this@asRxJava3.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.rxjava3.core.SingleSource<out T>.asReaktive(): Single<T> =
    singleUnsafe { observer ->
        subscribe(observer.asRxJava3())
    }

fun <T> io.reactivex.rxjava3.core.SingleObserver<in T>.asReaktive(): SingleObserver<T> =
    object : SingleObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava3())
        }

        override fun onSuccess(value: T) {
            this@asReaktive.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asReaktive.onError(error)
        }
    }

fun <T> SingleObserver<T>.asRxJava3(): io.reactivex.rxjava3.core.SingleObserver<T> =
    object : io.reactivex.rxjava3.core.SingleObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3.onSubscribe(disposable.asReaktive())
        }

        override fun onSuccess(value: T) {
            this@asRxJava3.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asRxJava3.onError(error)
        }
    }
