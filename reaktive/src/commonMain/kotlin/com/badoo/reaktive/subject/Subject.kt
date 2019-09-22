package com.badoo.reaktive.subject

import com.badoo.reaktive.observable.ObservableCallbacks

interface Subject<T> : Relay<T>, ObservableCallbacks<T> {

    val status: Status

    sealed class Status {
        object Active : Status()
        object Completed : Status()
        data class Error(val error: Throwable) : Status()
    }
}