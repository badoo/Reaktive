package com.badoo.reaktive.maybe

inline fun <T> maybe(crossinline onSubscribe: (observer: MaybeObserver<T>) -> Unit): Maybe<T> =
    object : Maybe<T> {
        override fun subscribe(observer: MaybeObserver<T>) {
            onSubscribe(observer)
        }
    }

fun <T> maybeOf(value: T): Maybe<T> =
    maybeByEmitter { emitter ->
        emitter.onSuccess(value)
    }

fun <T> T.toMaybe(): Maybe<T> =
    maybeByEmitter { emitter ->
        emitter.onSuccess(this)
    }

fun <T> errorMaybe(e: Throwable): Maybe<T> =
    maybeByEmitter { emitter ->
        emitter.onError(e)
    }

fun <T> Throwable.toErrorMaybe(): Maybe<T> = errorMaybe(this)

fun <T> emptyMaybe(): Maybe<T> =
    maybeByEmitter(MaybeEmitter<*>::onComplete)

fun <T> maybeFromFunction(func: () -> T): Maybe<T> =
    maybeByEmitter { emitter ->
        emitter.onSuccess(func())
    }