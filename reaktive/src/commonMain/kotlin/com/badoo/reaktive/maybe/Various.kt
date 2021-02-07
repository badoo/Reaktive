package com.badoo.reaktive.maybe

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.plugin.onAssembleMaybe
import kotlin.native.concurrent.SharedImmutable

@OptIn(ExperimentalReaktiveApi::class)
inline fun <T> maybeUnsafe(crossinline onSubscribe: (observer: MaybeObserver<T>) -> Unit): Maybe<T> =
    onAssembleMaybe(
        object : Maybe<T> {
            override fun subscribe(observer: MaybeObserver<T>) {
                onSubscribe(observer)
            }
        }
    )

fun <T> maybeOf(value: T): Maybe<T> =
    maybeUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onSuccess(value)
        }
    }

fun <T> T.toMaybe(): Maybe<T> = maybeOf(this)

fun <T : Any> maybeOfNotNull(value: T?): Maybe<T> =
    maybeUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            if (value == null) {
                observer.onComplete()
            } else {
                observer.onSuccess(value)
            }
        }
    }

fun <T : Any> T?.toMaybeNotNull(): Maybe<T> = maybeOfNotNull(this)

fun <T> maybeOfError(error: Throwable): Maybe<T> =
    maybeUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

fun <T> Throwable.toMaybeOfError(): Maybe<T> = maybeOfError(this)

@SharedImmutable
private val maybeOfEmpty by lazy {
    maybeUnsafe<Nothing> { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onComplete()
        }
    }
}

fun <T> maybeOfEmpty(): Maybe<T> = maybeOfEmpty

@SharedImmutable
private val maybeOfNever by lazy {
    maybeUnsafe<Nothing> { observer ->
        observer.onSubscribe(Disposable())
    }
}

fun <T> maybeOfNever(): Maybe<T> = maybeOfNever

fun <T> maybeFromFunction(func: () -> T): Maybe<T> =
    maybe { emitter ->
        emitter.onSuccess(func())
    }
