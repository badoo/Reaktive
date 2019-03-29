package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun <T> Maybe<T>.asObservable(): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : MaybeObserver<T>, Observer by observer, CompletableCallbacks by observer {
                override fun onSuccess(value: T) {
                    observer.onNext(value)
                    observer.onComplete()
                }
            }
        )
    }