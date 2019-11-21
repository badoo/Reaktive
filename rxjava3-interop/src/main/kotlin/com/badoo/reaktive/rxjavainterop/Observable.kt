package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observableUnsafe

fun <T> Observable<T>.asRxJava3Source(): io.reactivex.rxjava3.core.ObservableSource<T> =
    io.reactivex.rxjava3.core.ObservableSource { observer ->
        subscribe(observer.asReaktive())
    }

fun <T> Observable<T>.asRxJava3(): io.reactivex.rxjava3.core.Observable<T> =
    object : io.reactivex.rxjava3.core.Observable<T>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.Observer<in T>) {
            this@asRxJava3.subscribe(observer.asReaktive())
        }
    }

fun <T> io.reactivex.rxjava3.core.ObservableSource<out T>.asReaktive(): Observable<T> =
    observableUnsafe { observer ->
        subscribe(observer.asRxJava3())
    }

fun <T> io.reactivex.rxjava3.core.Observer<in T>.asReaktive(): ObservableObserver<T> =
    object : ObservableObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktive.onSubscribe(disposable.asRxJava3())
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

fun <T> ObservableObserver<T>.asRxJava3(): io.reactivex.rxjava3.core.Observer<T> =
    object : io.reactivex.rxjava3.core.Observer<T> {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3.onSubscribe(disposable.asReaktive())
        }

        override fun onNext(value: T) {
            this@asRxJava3.onNext(value)
        }

        override fun onComplete() {
            this@asRxJava3.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava3.onError(error)
        }
    }
