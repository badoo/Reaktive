package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observableUnsafe

fun <T : Any> Observable<T>.asRxJava3ObservableSource(): io.reactivex.rxjava3.core.ObservableSource<T> =
    io.reactivex.rxjava3.core.ObservableSource { observer ->
        subscribe(observer.asReaktiveObservableObserver())
    }

@Deprecated(message = "Use asRxJava3ObservableSource", replaceWith = ReplaceWith("asRxJava3ObservableSource()"))
fun <T : Any> Observable<T>.asRxJava3Source(): io.reactivex.rxjava3.core.ObservableSource<T> = asRxJava3ObservableSource()

fun <T : Any> Observable<T>.asRxJava3Observable(): io.reactivex.rxjava3.core.Observable<T> =
    object : io.reactivex.rxjava3.core.Observable<T>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.Observer<in T>) {
            this@asRxJava3Observable.subscribe(observer.asReaktiveObservableObserver())
        }
    }

@Deprecated(message = "Use asRxJava3Observable", replaceWith = ReplaceWith("asRxJava3Observable()"))
fun <T : Any> Observable<T>.asRxJava3(): io.reactivex.rxjava3.core.Observable<T> = asRxJava3Observable()

fun <T : Any> io.reactivex.rxjava3.core.ObservableSource<out T>.asReaktiveObservable(): Observable<T> =
    observableUnsafe { observer ->
        subscribe(observer.asRxJava3Observer())
    }

@Deprecated(message = "Use asReaktiveObservable", replaceWith = ReplaceWith("asReaktiveObservable()"))
fun <T : Any> io.reactivex.rxjava3.core.ObservableSource<out T>.asReaktive(): Observable<T> = asReaktiveObservable()

fun <T : Any> io.reactivex.rxjava3.core.Observer<in T>.asReaktiveObservableObserver(): ObservableObserver<T> =
    object : ObservableObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveObservableObserver.onSubscribe(disposable.asRxJava3Disposable())
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

@Deprecated(message = "Use asReaktiveObservableObserver", replaceWith = ReplaceWith("asReaktiveObservableObserver()"))
fun <T : Any> io.reactivex.rxjava3.core.Observer<in T>.asReaktive(): ObservableObserver<T> = asReaktiveObservableObserver()

fun <T : Any> ObservableObserver<T>.asRxJava3Observer(): io.reactivex.rxjava3.core.Observer<T> =
    object : io.reactivex.rxjava3.core.Observer<T> {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3Observer.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onNext(value: T) {
            this@asRxJava3Observer.onNext(value)
        }

        override fun onComplete() {
            this@asRxJava3Observer.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava3Observer.onError(error)
        }
    }

@Deprecated(message = "Use asRxJava3Observer", replaceWith = ReplaceWith("asRxJava3Observer()"))
fun <T : Any> ObservableObserver<T>.asRxJava3(): io.reactivex.rxjava3.core.Observer<T> = asRxJava3Observer()
