package com.badoo.reaktive.single

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.plugin.onAssembleSingle
import kotlin.native.concurrent.SharedImmutable

/**
 * ⚠️ Advanced use only: creates an instance of [Single] without any safeguards by calling `onSubscribe` with a [SingleObserver].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#unsafeCreate-io.reactivex.SingleSource-).
 */
@OptIn(ExperimentalReaktiveApi::class)
inline fun <T> singleUnsafe(crossinline onSubscribe: (observer: SingleObserver<T>) -> Unit): Single<T> =
    onAssembleSingle(
        object : Single<T> {
            override fun subscribe(observer: SingleObserver<T>) {
                onSubscribe(observer)
            }
        }
    )

/**
 * Returns a [Single] that emits the specified [value].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#just-T-).
 */
fun <T> singleOf(value: T): Single<T> =
    singleUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onSuccess(value)
        }
    }

/**
 * A convenience extensions function for [singleOf].
 */
fun <T> T.toSingle(): Single<T> = singleOf(this)

@SharedImmutable
private val singleOfNever by lazy {
    singleUnsafe<Nothing> { observer ->
        observer.onSubscribe(Disposable())
    }
}

/**
 * Returns a [Single] that never signals.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#never--).
 */
fun <T> singleOfNever(): Single<T> = singleOfNever

/**
 * Returns a [Single] that signals the specified [error] via `onError`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#error-java.lang.Throwable-).
 */
fun <T> singleOfError(error: Throwable): Single<T> =
    singleUnsafe { observer ->
        val disposable = Disposable()
        observer.onSubscribe(disposable)

        if (!disposable.isDisposed) {
            observer.onError(error)
        }
    }

/**
 * A convenience extensions function for [singleOfError].
 */
fun <T> Throwable.toSingleOfError(): Single<T> = singleOfError(this)

/**
 * Returns a [Single] that emits the value returned by the [func] shared function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#fromCallable-java.util.concurrent.Callable-).
 */
fun <T> singleFromFunction(func: () -> T): Single<T> =
    single { emitter ->
        emitter.onSuccess(func())
    }

/**
 * A convenience extensions function for [singleFromFunction].
 */
fun <T> (() -> T).asSingle(): Single<T> = singleFromFunction(this)
