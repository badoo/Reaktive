package com.badoo.reaktive.subject

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks

/**
 * Represents an [Observable] and [ObservableCallbacks] at the same time. The [Subject] interface
 * extends the [Relay] interface, adding the `onError` and `onComplete` callbacks through
 * the [ObservableCallbacks] interface.
 *
 * The [Subject] interface does not extend the [Observer] interface, so a [Subject] can not be
 * directly subscribed to an [Observable]. Use [Subject.getObserver] extension function for this.
 *
 * All [Subject]s provided by Reaktive are thread safe by default.
 *
 * See [Relay] and [ObservableCallbacks] for more information.
 */
interface Subject<T> : Relay<T>, ObservableCallbacks<T> {

    /**
     * Returns the current [Status] of the [Subject]
     */
    val status: Status

    sealed class Status {

        /**
         * The [Subject] is active and can accept signals
         */
        object Active : Status()

        /**
         * The [Subject] has received `onComplete` signal and is no longer active
         */
        object Completed : Status()

        /**
         * The [Subject] has received `onError` signal and is no longer active
         */
        data class Error(val error: Throwable) : Status()
    }
}
