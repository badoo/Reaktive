package com.badoo.reaktive.completable

import com.badoo.reaktive.base.Subscribable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun <T> Completable.asObservable(): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : CompletableObserver, Subscribable by observer, CompletableCallbacks by observer {
            }
        )
    }