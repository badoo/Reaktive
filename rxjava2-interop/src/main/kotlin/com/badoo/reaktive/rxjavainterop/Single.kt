package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.single

fun <T> Single<T>.toRxJava2Source(): io.reactivex.SingleSource<T> =
    io.reactivex.SingleSource { observer ->
        subscribe(observer.toReaktive())
    }

fun <T> Single<T>.toRxJava2(): io.reactivex.Single<T> =
    object : io.reactivex.Single<T>() {
        override fun subscribeActual(observer: io.reactivex.SingleObserver<in T>) {
            this@toRxJava2.subscribe(observer.toReaktive())
        }
    }

fun <T> io.reactivex.SingleSource<out T>.toReaktive(): Single<T> =
    single { observer ->
        subscribe(observer.toRxJava2())
    }

fun <T> io.reactivex.SingleObserver<in T>.toReaktive(): SingleObserver<T> =
    object : SingleObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@toReaktive.onSubscribe(disposable.toRxJava2())
        }

        override fun onSuccess(value: T) {
            this@toReaktive.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@toReaktive.onError(error)
        }
    }

fun <T> SingleObserver<T>.toRxJava2(): io.reactivex.SingleObserver<T> =
    object : io.reactivex.SingleObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@toRxJava2.onSubscribe(disposable.toReaktive())
        }

        override fun onSuccess(value: T) {
            this@toRxJava2.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@toRxJava2.onError(error)
        }
    }