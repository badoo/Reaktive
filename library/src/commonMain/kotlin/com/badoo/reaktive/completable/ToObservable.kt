package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun <T> Completable.toObservable(): Observable<T> =
    observable { observer ->
        subscribeSafe(observer)
    }
