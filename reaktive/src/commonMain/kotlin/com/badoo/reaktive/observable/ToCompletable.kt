package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completableUnsafe

fun Observable<*>.toCompletable(): Completable =
    completableUnsafe { observer ->
        subscribeSafe(
            object : ObservableObserver<Any?>, Observer by observer, CompletableCallbacks by observer {
                override fun onNext(value: Any?) {
                    // no-op
                }
            }
        )
    }