package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completableUnsafe

fun Single<*>.asCompletable(): Completable =
    completableUnsafe { observer ->
        subscribeSafe(
            object : SingleObserver<Any?>, Observer by observer, ErrorCallback by observer {
                override fun onSuccess(value: Any?) {
                    observer.onComplete()
                }
            }
        )
    }
