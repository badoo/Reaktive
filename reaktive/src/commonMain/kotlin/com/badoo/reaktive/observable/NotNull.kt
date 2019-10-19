package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.completable.CompletableCallbacks

fun <T : Any> Observable<T?>.notNull(): Observable<T> =
    observableUnsafe { observer ->
        subscribeSafe(
            object : ObservableObserver<T?>, Observer by observer, CompletableCallbacks by observer {
                override fun onNext(value: T?) {
                    if (value != null) {
                        observer.onNext(value)
                    }
                }
            }
        )
    }
