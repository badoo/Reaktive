package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableUnsafe

fun <T> Completable.asObservable(): Observable<T> =
    observableUnsafe { observer ->
        subscribeSafe(
            object : CompletableObserver, Observer by observer, CompletableCallbacks by observer {
            }
        )
    }