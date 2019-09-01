package com.badoo.reaktive.maybe

inline fun <T> maybeUnsafe(crossinline onSubscribe: (observer: MaybeObserver<T>) -> Unit): Maybe<T> =
    object : Maybe<T> {
        override fun subscribe(observer: MaybeObserver<T>) {
            onSubscribe(observer)
        }
    }

fun <T : Any> maybeOf(value: T?): Maybe<T> =
    maybe { emitter ->
        if (value == null) {
            emitter.onComplete()
        } else {
            emitter.onSuccess(value)
        }
    }

fun <T : Any> T?.toMaybe(): Maybe<T> = maybeOf(this)

fun <T> maybeOfError(error: Throwable): Maybe<T> =
    maybe { emitter ->
        emitter.onError(error)
    }

fun <T> Throwable.toMaybeOfError(): Maybe<T> = maybeOfError(this)

fun <T> maybeOfEmpty(): Maybe<T> =
    maybe(MaybeEmitter<*>::onComplete)

fun <T> maybeOfNever(): Maybe<T> = maybe {}

fun <T> maybeFromFunction(func: () -> T): Maybe<T> =
    maybe { emitter ->
        emitter.onSuccess(func())
    }