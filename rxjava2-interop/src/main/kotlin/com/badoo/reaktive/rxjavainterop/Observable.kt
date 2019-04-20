package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observableUnsafe

fun <T> Observable<T>.toRxJava2Source(): io.reactivex.ObservableSource<T> =
    io.reactivex.ObservableSource { observer ->
        subscribe(observer.toReaktive())
    }

fun <T> Observable<T>.toRxJava2(): io.reactivex.Observable<T> =
    object : io.reactivex.Observable<T>() {
        override fun subscribeActual(observer: io.reactivex.Observer<in T>) {
            this@toRxJava2.subscribe(observer.toReaktive())
        }
    }

fun <T> io.reactivex.ObservableSource<out T>.toReaktive(): Observable<T> =
    observableUnsafe { observer ->
        subscribe(observer.toRxJava2())
    }

fun <T> io.reactivex.Observer<in T>.toReaktive(): ObservableObserver<T> =
    object : ObservableObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@toReaktive.onSubscribe(disposable.toRxJava2())
        }

        override fun onNext(value: T) {
            this@toReaktive.onNext(value)
        }

        override fun onComplete() {
            this@toReaktive.onComplete()
        }

        override fun onError(error: Throwable) {
            this@toReaktive.onError(error)
        }
    }

fun <T> ObservableObserver<T>.toRxJava2(): io.reactivex.Observer<T> =
    object : io.reactivex.Observer<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@toRxJava2.onSubscribe(disposable.toReaktive())
        }

        override fun onNext(value: T) {
            this@toRxJava2.onNext(value)
        }

        override fun onComplete() {
            this@toRxJava2.onComplete()
        }

        override fun onError(error: Throwable) {
            this@toRxJava2.onError(error)
        }
    }