package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.singleUnsafe

fun <T, C> Observable<T>.collect(collection: C, accumulator: (C, T) -> Unit): Single<C> =
    singleUnsafe { observer ->
        subscribeSafe(
            object : ObservableObserver<T>, Observer by observer, ErrorCallback by observer {
                override fun onNext(value: T) {
                    accumulator(collection, value)
                }

                override fun onComplete() {
                    observer.onSuccess(collection)
                }
            }
        )
    }