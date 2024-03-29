package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observableUnsafe

fun <T : Any> Observable<T>.asRxJava2Observable(): io.reactivex.Observable<T> =
    object : io.reactivex.Observable<T>() {
        override fun subscribeActual(observer: io.reactivex.Observer<in T>) {
            this@asRxJava2Observable.subscribe(observer.asReaktiveObservableObserver())
        }
    }

fun <T : Any> io.reactivex.ObservableSource<out T>.asReaktiveObservable(): Observable<T> =
    observableUnsafe { observer ->
        subscribe(observer.asRxJava2Observer())
    }

fun <T : Any> io.reactivex.Observer<in T>.asReaktiveObservableObserver(): ObservableObserver<T> =
    object : ObservableObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveObservableObserver.onSubscribe(disposable.asRxJava2Disposable())
        }

        override fun onNext(value: T) {
            this@asReaktiveObservableObserver.onNext(value)
        }

        override fun onComplete() {
            this@asReaktiveObservableObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktiveObservableObserver.onError(error)
        }
    }

fun <T : Any> ObservableObserver<T>.asRxJava2Observer(): io.reactivex.Observer<T> =
    object : io.reactivex.Observer<T> {
        override fun onSubscribe(disposable: io.reactivex.disposables.Disposable) {
            this@asRxJava2Observer.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onNext(value: T) {
            this@asRxJava2Observer.onNext(value)
        }

        override fun onComplete() {
            this@asRxJava2Observer.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava2Observer.onError(error)
        }
    }
