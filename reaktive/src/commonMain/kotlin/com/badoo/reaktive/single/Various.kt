package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable

inline fun <T> singleUnsafe(crossinline onSubscribe: (observer: SingleObserver<T>) -> Unit): Single<T> =
    object : Single<T> {
        override fun subscribe(observer: SingleObserver<T>) {
            onSubscribe(observer)
        }
    }

fun <T> singleOf(value: T): Single<T> =
    singleUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onSuccess(value)
        }
    }

fun <T> T.toSingle(): Single<T> = singleOf(this)

fun <T> singleOfNever(): Single<T> =
    singleUnsafe { observer ->
        observer.onSubscribe(Disposable())
    }

fun <T> singleOfError(error: Throwable): Single<T> =
    singleUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

fun <T> Throwable.toSingleOfError(): Single<T> = singleOfError(this)

fun <T> singleFromFunction(func: () -> T): Single<T> =
    single { emitter ->
        emitter.onSuccess(func())
    }

fun <T> (() -> T).asSingle(): Single<T> = singleFromFunction(this)
