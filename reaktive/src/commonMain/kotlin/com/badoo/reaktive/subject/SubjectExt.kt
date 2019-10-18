package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver

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

val Subject<*>.isActive: Boolean get() = status is Subject.Status.Active
