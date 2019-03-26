package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Subscribable
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completable

fun Observable<*>.toCompletable(): Completable =
    completable { observer ->
        subscribeSafe(
            object : ObservableObserver<Any?>, Subscribable by observer, CompletableCallbacks by observer {
                override fun onNext(value: Any?) {
                    // no-op
                }
            }
        )
    }