package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completable

fun Observable<*>.asCompletable(): Completable =
    completable { observer ->
        subscribeSafe(
            object : ObservableObserver<Any?>, Observer by observer, CompletableCallbacks by observer {
                override fun onNext(value: Any?) {
                    // no-op
                }
            }
        )
    }