package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observableUnsafe

fun <T> Observable<T>.asRxJava2Source(): io.reactivex.ObservableSource<T> =
    io.reactivex.ObservableSource { observer ->
        subscribe(observer.asReaktive())
    }

fun <T> Observable<T>.asRxJava2(): io.reactivex.Observable<T> =
    object : io.reactivex.Observable<T>() {
        override fun subscribeActual(observer: io.reactivex.Observer<in T>) {
            this@asRxJava2.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.ObservableSource<out T>.asReaktive(): Observable<T> =
    observableUnsafe { observer ->
        subscribe(observer.asRxJava2())
    }

fun <T> io.reactivex.Observer<in T>.asReaktive(): ObservableObserver<T> =
    object : ObservableObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava2())
        }

        override fun onNext(value: T) {
            this@asReaktive.onNext(value)
        }

        override fun onComplete() {
            this@asReaktive.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktive.onError(error)
        }
    }

fun <T> ObservableObserver<T>.asRxJava2(): io.reactivex.Observer<T> =
    object : io.reactivex.Observer<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2.onSubscribe(disposable.asReaktive())
        }

        override fun onNext(value: T) {
            this@asRxJava2.onNext(value)
        }

        override fun onComplete() {
            this@asRxJava2.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava2.onError(error)
        }
    }
