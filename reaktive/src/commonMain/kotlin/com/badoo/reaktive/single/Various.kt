package com.badoo.reaktive.single

inline fun <T> singleUnsafe(crossinline onSubscribe: (observer: SingleObserver<T>) -> Unit): Single<T> =
    object : Single<T> {
        override fun subscribe(observer: SingleObserver<T>) {
            onSubscribe(observer)
        }
    }

fun <T> singleOf(value: T): Single<T> =
    single { emitter ->
        emitter.onSuccess(value)
    }

fun <T> T.toSingle(): Single<T> = singleOf(this)

fun <T> singleOfNever(): Single<T> = single {}

fun <T> singleOfError(error: Throwable): Single<T> =
    single { emitter ->
        emitter.onError(error)
    }

fun <T> Throwable.toSingleOfError(): Single<T> = singleOfError(this)

fun <T> singleFromFunction(func: () -> T): Single<T> =
    single { emitter ->
        emitter.onSuccess(func())
    }

fun <T> (() -> T).asSingle(): Single<T> = singleFromFunction(this)