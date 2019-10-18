package com.badoo.reaktive.observable

inline fun <T> observableUnsafe(crossinline onSubscribe: (observer: ObservableObserver<T>) -> Unit): Observable<T> =
    object : Observable<T> {
        override fun subscribe(observer: ObservableObserver<T>) {
            onSubscribe(observer)
        }
    }

fun <T> observableOf(value: T): Observable<T> =
    observable { emitter ->
        emitter.onNext(value)
        emitter.onComplete()
    }

fun <T> T.toObservable(): Observable<T> = observableOf(this)

fun <T> Iterable<T>.asObservable(): Observable<T> =
    observable { emitter ->
        forEach(emitter::onNext)
        emitter.onComplete()
    }

fun <T> observableOf(vararg values: T): Observable<T> =
    observable { emitter ->
        values.forEach(emitter::onNext)
        emitter.onComplete()
    }

fun <T> observableOfError(error: Throwable): Observable<T> =
    observable { emitter -> emitter.onError(error) }

fun <T> Throwable.toObservableOfError(): Observable<T> = observableOfError(this)

fun <T> observableOfEmpty(): Observable<T> = observable(ObservableEmitter<*>::onComplete)

fun <T> observableOfNever(): Observable<T> = observable {}

fun <T> observableFromFunction(func: () -> T): Observable<T> =
    observable { emitter ->
        emitter.onNext(func())
        emitter.onComplete()
    }
    