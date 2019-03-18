package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.CompletableObserver

/**
 * Represents [Observer] of source that produces stream of values
 */
interface ObservableObserver<in T> : CompletableObserver {

    /**
     * Notifies the [Observer] about new value.
     * Note that this method as well as all other methods must never be called in parallel.
     */
    fun onNext(value: T)
}