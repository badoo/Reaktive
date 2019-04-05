package com.badoo.reaktive.single

inline fun <T> single(crossinline onSubscribe: (observer: SingleObserver<T>) -> Unit): Single<T> =
    object : Single<T> {
        override fun subscribe(observer: SingleObserver<T>) {
            onSubscribe(observer)
        }
    }

fun <T> singleOf(value: T): Single<T> =
    singleByEmitter { emitter ->
        emitter.onSuccess(value)
    }

fun <T> T.toSingle(): Single<T> = singleOf(this)

fun <T> errorSingle(error: Throwable): Single<T> =
    singleByEmitter { emitter ->
        emitter.onError(error)
    }

fun <T> Throwable.toErrorSingle(): Single<T> = errorSingle(this)

fun <T> singleFromFunction(func: () -> T): Single<T> =
    singleByEmitter { emitter ->
        emitter.onSuccess(func())
    }