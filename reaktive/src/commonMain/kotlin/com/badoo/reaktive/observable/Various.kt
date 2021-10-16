package com.badoo.reaktive.observable

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.plugin.onAssembleObservable
import kotlin.native.concurrent.SharedImmutable

/**
 * ⚠️ Advanced use only: creates an instance of [Observable] without any safeguards by calling `onSubscribe` with an [ObservableObserver].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#unsafeCreate-io.reactivex.ObservableSource-).
 */
@OptIn(ExperimentalReaktiveApi::class)
inline fun <T> observableUnsafe(crossinline onSubscribe: (observer: ObservableObserver<T>) -> Unit): Observable<T> =
    onAssembleObservable(
        object : Observable<T> {
            override fun subscribe(observer: ObservableObserver<T>) {
                onSubscribe(observer)
            }
        }
    )

/**
 * Returns an [Observable] that emits the specified [value].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#just-T-).
 */
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

/**
 * A convenience extensions function for [observableOf].
 */
fun <T> T.toObservable(): Observable<T> = observableOf(this)

/**
 * Returns an [Observable] that emits all elements from the source [Iterable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#fromIterable-java.lang.Iterable-).
 */
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

/**
 * Returns an [Observable] that emits all elements from the [values] array.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#fromArray-T...-).
 */
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

/**
 * Returns an [Observable] that signals the specified [error] via `onError`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#error-java.lang.Throwable-).
 */
fun <T> observableOfError(error: Throwable): Observable<T> =
    observableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

/**
 * A convenience extensions function for [observableOfError].
 */
fun <T> Throwable.toObservableOfError(): Observable<T> = observableOfError(this)

@SharedImmutable
private val observableOfEmpty by lazy {
    observableUnsafe<Nothing> { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onComplete()
        }
    }
}

/**
 * Returns an [Observable] that signals `onComplete`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#empty--).
 */
fun <T> observableOfEmpty(): Observable<T> = observableOfEmpty

@SharedImmutable
private val observableOfNever by lazy {
    observableUnsafe<Nothing> { observer ->
        observer.onSubscribe(Disposable())
    }
}

/**
 * Returns a [Observable] that never signals.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#never--).
 */
fun <T> observableOfNever(): Observable<T> = observableOfNever

/**
 * Returns an [Observable] that emits the value returned by the [func] shared function and completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#fromCallable-java.util.concurrent.Callable-).
 */
fun <T> observableFromFunction(func: () -> T): Observable<T> =
    observable { emitter ->
        emitter.onNext(func())
        emitter.onComplete()
    }
