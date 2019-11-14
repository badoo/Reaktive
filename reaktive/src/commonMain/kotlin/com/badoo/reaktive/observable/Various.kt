package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable

inline fun <T> observableUnsafe(crossinline onSubscribe: (observer: ObservableObserver<T>) -> Unit): Observable<T> =
    object : Observable<T> {
        override fun subscribe(observer: ObservableObserver<T>) {
            onSubscribe(observer)
        }
    }

fun <T> observableOf(value: T): Observable<T> =
    observableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onNext(value)
            if (!disposable.isDisposed) {
                observer.onComplete()
            }
        }
    }

fun <T> T.toObservable(): Observable<T> = observableOf(this)

fun <T> Iterable<T>.asObservable(): Observable<T> =
    observableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            forEach {
                observer.onNext(it)
                if (disposable.isDisposed) {
                    return@observableUnsafe
                }
            }

            observer.onComplete()
        }
    }

fun <T> observableOf(vararg values: T): Observable<T> =
    observableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            values.forEach {
                observer.onNext(it)
                if (disposable.isDisposed) {
                    return@observableUnsafe
                }
            }

            observer.onComplete()
        }
    }

fun <T> observableOfError(error: Throwable): Observable<T> =
    observableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

fun <T> Throwable.toObservableOfError(): Observable<T> = observableOfError(this)

fun <T> observableOfEmpty(): Observable<T> =
    observableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onComplete()
        }
    }

fun <T> observableOfNever(): Observable<T> =
    observableUnsafe { observer ->
        observer.onSubscribe(Disposable())
    }

fun <T> observableFromFunction(func: () -> T): Observable<T> =
    observable { emitter ->
        emitter.onNext(func())
        emitter.onComplete()
    }
