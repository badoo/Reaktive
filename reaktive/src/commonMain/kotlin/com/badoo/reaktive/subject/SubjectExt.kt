package com.badoo.reaktive.subject

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver

fun <T> Subject<T>.getObserver(): ObservableObserver<T> =
    object : ObservableObserver<T>, ObservableCallbacks<T> by this {
        override fun onSubscribe(disposable: Disposable) {
            if (status !is Subject.Status.Active) {
                disposable.dispose()
            }
        }
    }