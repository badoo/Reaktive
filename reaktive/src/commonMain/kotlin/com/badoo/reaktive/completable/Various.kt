package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.plugin.onAssembleCompletable
import kotlin.native.concurrent.SharedImmutable

/**
 * ⚠️ Advanced use only: creates an instance of [Completable] without any safeguards by calling `onSubscribe` with a [CompletableObserver].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#unsafeCreate-io.reactivex.CompletableSource-).
 */
@OptIn(ExperimentalReaktiveApi::class)
inline fun completableUnsafe(crossinline onSubscribe: (observer: CompletableObserver) -> Unit): Completable =
    onAssembleCompletable(
        object : Completable {
            override fun subscribe(observer: CompletableObserver) {
                onSubscribe(observer)
            }
        }
    )

/**
 * Returns a [Completable] that signals the specified [error] via `onError`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#error-java.lang.Throwable-).
 */
fun completableOfError(error: Throwable): Completable =
    completableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

/**
 * A convenience extensions function for [completableOfError].
 */
fun Throwable.toCompletableOfError(): Completable = completableOfError(this)

@SharedImmutable
private val completableOfEmpty by lazy {
    completableUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onComplete()
        }
    }
}

/**
 * Returns a [Completable] that signals `onComplete`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#complete--).
 */
fun completableOfEmpty(): Completable = completableOfEmpty

@SharedImmutable
private val completableOfNever by lazy {
    completableUnsafe { observer ->
        observer.onSubscribe(Disposable())
    }
}

/**
 * Returns a [Completable] that never terminates.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#never--).
 */
fun completableOfNever(): Completable = completableOfNever

/**
 * Returns a [Completable] that calls the [func] shared function and then completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#fromCallable-java.util.concurrent.Callable-).
 */
fun completableFromFunction(func: () -> Unit): Completable =
    completable { emitter ->
        func()
        emitter.onComplete()
    }
