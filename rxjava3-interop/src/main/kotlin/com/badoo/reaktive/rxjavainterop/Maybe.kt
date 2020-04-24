package com.badoo.reaktive.rxjavainterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.maybeUnsafe

fun <T : Any> Maybe<T>.asRxJava3MaybeSource(): io.reactivex.rxjava3.core.MaybeSource<T> =
    io.reactivex.rxjava3.core.MaybeSource { observer ->
        subscribe(observer.asReaktiveMaybeObserver())
    }

@Deprecated(message = "Use asRxJava3MaybeSource", replaceWith = ReplaceWith("asRxJava3MaybeSource()"))
fun <T : Any> Maybe<T>.asRxJava3Source(): io.reactivex.rxjava3.core.MaybeSource<T> = asRxJava3MaybeSource()

fun <T : Any> Maybe<T>.asRxJava3Maybe(): io.reactivex.rxjava3.core.Maybe<T> =
    object : io.reactivex.rxjava3.core.Maybe<T>() {
        override fun subscribeActual(observer: io.reactivex.rxjava3.core.MaybeObserver<in T>) {
            this@asRxJava3Maybe.subscribe(observer.asReaktiveMaybeObserver())
        }
    }

@Deprecated(message = "Use asRxJava3Maybe", replaceWith = ReplaceWith("asRxJava3Maybe()"))
fun <T : Any> Maybe<T>.asRxJava3(): io.reactivex.rxjava3.core.Maybe<T> = asRxJava3Maybe()

fun <T : Any> io.reactivex.rxjava3.core.MaybeSource<out T>.asReaktiveMaybe(): Maybe<T> =
    maybeUnsafe { observer ->
        subscribe(observer.asRxJava3MaybeObserver())
    }

@Deprecated(message = "Use asReaktiveMaybe", replaceWith = ReplaceWith("asReaktiveMaybe()"))
fun <T : Any> io.reactivex.rxjava3.core.MaybeSource<out T>.asReaktive(): Maybe<T> = asReaktiveMaybe()

fun <T : Any> io.reactivex.rxjava3.core.MaybeObserver<in T>.asReaktiveMaybeObserver(): MaybeObserver<T> =
    object : MaybeObserver<T> {
        override fun onSubscribe(disposable: Disposable) {
            this@asReaktiveMaybeObserver.onSubscribe(disposable.asRxJava3Disposable())
        }

        override fun onSuccess(value: T) {
            this@asReaktiveMaybeObserver.onSuccess(value)
        }

        override fun onComplete() {
            this@asReaktiveMaybeObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asReaktiveMaybeObserver.onError(error)
        }
    }

@Deprecated(message = "Use asReaktiveMaybeObserver", replaceWith = ReplaceWith("asReaktiveMaybeObserver()"))
fun <T : Any> io.reactivex.rxjava3.core.MaybeObserver<in T>.asReaktive(): MaybeObserver<T> = asReaktiveMaybeObserver()

fun <T : Any> MaybeObserver<T>.asRxJava3MaybeObserver(): io.reactivex.rxjava3.core.MaybeObserver<T> =
    object : io.reactivex.rxjava3.core.MaybeObserver<T> {
        override fun onSubscribe(disposable: io.reactivex.rxjava3.disposables.Disposable) {
            this@asRxJava3MaybeObserver.onSubscribe(disposable.asReaktiveDisposable())
        }

        override fun onSuccess(value: T) {
            this@asRxJava3MaybeObserver.onSuccess(value)
        }

        override fun onComplete() {
            this@asRxJava3MaybeObserver.onComplete()
        }

        override fun onError(error: Throwable) {
            this@asRxJava3MaybeObserver.onError(error)
        }
    }

@Deprecated(message = "Use asRxJava3MaybeObserver", replaceWith = ReplaceWith("asRxJava3MaybeObserver()"))
fun <T : Any> MaybeObserver<T>.asRxJava3(): io.reactivex.rxjava3.core.MaybeObserver<T> = asRxJava3MaybeObserver()
