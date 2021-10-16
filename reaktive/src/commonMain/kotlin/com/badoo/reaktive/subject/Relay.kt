package com.badoo.reaktive.subject

import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.observable.Observable

/**
 * Represents an [Observable] and a [ValueCallback] at the same time, which means
 * the [Relay] interface is both a producer and a consumer. Unlike [Subject], the [Relay] interface
 * does not have `onError` and `onComplete` callbacks. A [Relay] can only be supplied with
 * values via [ValueCallback.onNext] callback.
 *
 * The [Relay] interface is inspired by the [RxRelay](https://github.com/JakeWharton/RxRelay) library.
 *
 * See [Observable] and [ValueCallback] for more information.
 */
interface Relay<T> : Observable<T>, ValueCallback<T>
