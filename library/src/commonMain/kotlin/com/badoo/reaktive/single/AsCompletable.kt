package com.badoo.reaktive.single

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completable

fun Single<*>.asCompletable(): Completable =
    completable { observer ->
        subscribeSafe(
            object : SingleObserver<Any?>, Observer by observer {
                override fun onSuccess(value: Any?) {
                    observer.onComplete()
                }
            }
        )
    }