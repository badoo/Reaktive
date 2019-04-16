package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableUnsafe

fun <T> Single<T>.asObservable(): Observable<T> =
    observableUnsafe { observer ->
        subscribeSafe(
            object : SingleObserver<T>, Observer by observer, ErrorCallback by observer {
                override fun onSuccess(value: T) {
                    observer.onNext(value)
                    observer.onComplete()
                }
            }
        )
    }