package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.singleUnsafe

fun <T : Any> Single<T>.asRxJava2SingleSource(): io.reactivex.SingleSource<T> =
    io.reactivex.SingleSource { observer ->
        subscribe(observer.asReaktiveSingleObserver())
    }

fun <T : Any> Single<T>.asRxJava2Single(): io.reactivex.Single<T> =
    object : io.reactivex.Single<T>() {
        override fun subscribeActual(observer: io.reactivex.SingleObserver<in T>) {
            this@asRxJava2Single.subscribe(observer.asReaktiveSingleObserver())
        }
    }

fun <T : Any> io.reactivex.SingleSource<out T>.asReaktiveSingle(): Single<T> =
    singleUnsafe { observer ->
        subscribe(observer.asRxJava2SingleObserver())
    }

fun <T : Any> io.reactivex.SingleObserver<in T>.asReaktiveSingleObserver(): SingleObserver<T> =
    object : SingleObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveSingleObserver.onSubscribe(disposable.asRxJava2Disposable())
        }

        override fun onSuccess(value: T) {
            this@asReaktiveSingleObserver.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asReaktiveSingleObserver.onError(error)
        }
    }

fun <T : Any> SingleObserver<T>.asRxJava2SingleObserver(): io.reactivex.SingleObserver<T> =
    object : io.reactivex.SingleObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2SingleObserver.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onSuccess(value: T) {
            this@asRxJava2SingleObserver.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asRxJava2SingleObserver.onError(error)
        }
    }
