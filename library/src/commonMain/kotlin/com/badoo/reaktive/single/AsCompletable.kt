package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Subscribable
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completable

fun Single<*>.asCompletable(): Completable =
    completable { observer ->
        subscribeSafe(
            object : SingleObserver<Any?>, Subscribable by observer, ErrorCallback by observer {
                override fun onSuccess(value: Any?) {
                    observer.onComplete()
                }
            }
        )
    }