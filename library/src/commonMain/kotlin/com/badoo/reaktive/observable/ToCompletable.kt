package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completable

fun Observable<*>.toCompletable(): Completable =
    completable { observer ->
        subscribeSafe(
            object : ObservableObserver<Any?>, CompletableObserver by observer {
                override fun onNext(value: Any?) {
                    // no-op
                }
            }
        )
    }