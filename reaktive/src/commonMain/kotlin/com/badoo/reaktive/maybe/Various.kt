package com.badoo.reaktive.maybe

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.plugin.onAssembleMaybe
import kotlin.native.concurrent.SharedImmutable

/**
 * ⚠️ Advanced use only: creates an instance of [Maybe] without any safeguards by calling `onSubscribe` with a [MaybeObserver].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#unsafeCreate-io.reactivex.MaybeSource-).
 */
@OptIn(ExperimentalReaktiveApi::class)
inline fun <T> maybeUnsafe(crossinline onSubscribe: (observer: MaybeObserver<T>) -> Unit): Maybe<T> =
    onAssembleMaybe(
        object : Maybe<T> {
            override fun subscribe(observer: MaybeObserver<T>) {
                onSubscribe(observer)
            }
        }
    )

/**
 * Returns a [Maybe] that emits the specified [value]. The value is emitted even if it is `null`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#just-T-).
 */
fun <T> maybeOf(value: T): Maybe<T> =
    maybeUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onSuccess(value)
        }
    }

/**
 * A convenience extensions function for [maybeOf].
 */
fun <T> T.toMaybe(): Maybe<T> = maybeOf(this)

/**
 * Returns a [Maybe] that emits the provided [value] if it is not `null`, otherwise completes.
 */
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

/**
 * A convenience extensions function for [maybeOfNotNull].
 */
fun <T : Any> T?.toMaybeNotNull(): Maybe<T> = maybeOfNotNull(this)

/**
 * Returns a [Maybe] that signals the specified [error] via `onError`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#error-java.lang.Throwable-).
 */
fun <T> maybeOfError(error: Throwable): Maybe<T> =
    maybeUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

/**
 * A convenience extensions function for [maybeOfError].
 */
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

/**
 * Returns a [Maybe] that signals `onComplete`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#empty--).
 */
fun <T> maybeOfEmpty(): Maybe<T> = maybeOfEmpty

@SharedImmutable
private val maybeOfNever by lazy {
    maybeUnsafe<Nothing> { observer ->
        observer.onSubscribe(Disposable())
    }
}

/**
 * Returns a [Maybe] that never signals.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#never--).
 */
fun <T> maybeOfNever(): Maybe<T> = maybeOfNever

/**
 * Returns a [Maybe] that emits the value returned by the [func] shared function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#fromCallable-java.util.concurrent.Callable-).
 */
fun <T> maybeFromFunction(func: () -> T): Maybe<T> =
    maybe { emitter ->
        emitter.onSuccess(func())
    }
