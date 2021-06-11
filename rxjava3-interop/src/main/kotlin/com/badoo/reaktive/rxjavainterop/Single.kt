package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.singleUnsafe

fun <T : Any> Single<T>.asRxJava3SingleSource(): io.reactivex.rxjava3.core.SingleSource<T> =
    io.reactivex.rxjava3.core.SingleSource { observer ->
        subscribe(observer.asReaktiveSingleObserver())
    }

@Deprecated(
    message = "Use asRxJava3SingleSource",
    replaceWith = ReplaceWith("asRxJava3SingleSource()"),
    level = DeprecationLevel.ERROR
)
fun <T : Any> Single<T>.asRxJava3Source(): io.reactivex.rxjava3.core.SingleSource<T> = asRxJava3SingleSource()

fun <T : Any> Single<T>.asRxJava3Single(): io.reactivex.rxjava3.core.Single<T> =
    object : io.reactivex.rxjava3.core.Single<T>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.SingleObserver<in T>) {
            this@asRxJava3Single.subscribe(observer.asReaktiveSingleObserver())
        }
    }

@Deprecated(
    message = "Use asRxJava3Single",
    replaceWith = ReplaceWith("asRxJava3Single()"),
    level = DeprecationLevel.ERROR
)
fun <T : Any> Single<T>.asRxJava3(): io.reactivex.rxjava3.core.Single<T> = asRxJava3Single()

fun <T : Any> io.reactivex.rxjava3.core.SingleSource<out T>.asReaktiveSingle(): Single<T> =
    singleUnsafe { observer ->
        subscribe(observer.asRxJava3SingleObserver())
    }

@Deprecated(
    message = "Use asReaktiveSingle",
    replaceWith = ReplaceWith("asReaktiveSingle()"),
    level = DeprecationLevel.ERROR
)
fun <T : Any> io.reactivex.rxjava3.core.SingleSource<out T>.asReaktive(): Single<T> = asReaktiveSingle()

fun <T : Any> io.reactivex.rxjava3.core.SingleObserver<in T>.asReaktiveSingleObserver(): SingleObserver<T> =
    object : SingleObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveSingleObserver.onSubscribe(disposable.asRxJava3Disposable())
        }

        override fun onSuccess(value: T) {
            this@asReaktiveSingleObserver.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asReaktiveSingleObserver.onError(error)
        }
    }

@Deprecated(
    message = "Use asReaktiveSingleObserver",
    replaceWith = ReplaceWith("asReaktiveSingleObserver()"),
    level = DeprecationLevel.ERROR
)
fun <T : Any> io.reactivex.rxjava3.core.SingleObserver<in T>.asReaktive(): SingleObserver<T> = asReaktiveSingleObserver()

fun <T : Any> SingleObserver<T>.asRxJava3SingleObserver(): io.reactivex.rxjava3.core.SingleObserver<T> =
    object : io.reactivex.rxjava3.core.SingleObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3SingleObserver.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onSuccess(value: T) {
            this@asRxJava3SingleObserver.onSuccess(value)
        }

        override fun onError(error: Throwable) {
            this@asRxJava3SingleObserver.onError(error)
        }
    }

@Deprecated(
    message = "Use asRxJava3SingleObserver",
    replaceWith = ReplaceWith("asRxJava3SingleObserver()"),
    level = DeprecationLevel.ERROR
)
fun <T : Any> SingleObserver<T>.asRxJava3(): io.reactivex.rxjava3.core.SingleObserver<T> = asRxJava3SingleObserver()
