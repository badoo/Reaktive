package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver

/**
 * Creates and returns an [ObservableObserver] that can be used to subscribe this [Subject] to an [Observable].
 * All signals from all [Observable]s subscribed using the returned [ObservableObserver] are sent to this [Subject].
 *
 * Every time the returned [ObservableObserver] is subscribed to an [Observable], the [Disposable] returned
 * by the subscribed [Observable] is either emitted via the [onSubscribe] callback (if supplied and the [Subject] is still active),
 * or disposed (if the [Subject] is no longer active).
 */
fun <T> Subject<T>.getObserver(onSubscribe: ((Disposable) -> Unit)? = null): ObservableObserver<T> =
    object : ObservableObserver<T>, ObservableCallbacks<T> by this {
        override fun onSubscribe(disposable: Disposable) {
            if (isActive) {
                onSubscribe?.invoke(disposable)
            } else {
                disposable.dispose()
            }
        }
    }

/**
 * Returns `true` if the [Status][Subject.Status] is [Active][Subject.Status.Active], `false` otherwise
 */
val Subject.Status.isActive: Boolean get() = this is Subject.Status.Active

/**
 * Returns `true` if the [Subject]s [Status][Subject.Status] is [Active][Subject.Status.Active], `false` otherwise
 */
val Subject<*>.isActive: Boolean get() = status.isActive
