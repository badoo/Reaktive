package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch

fun <T, R> Single<T>.map(mapper: (T) -> R): Single<R> =
    singleUnsafe { observer ->
        subscribeSafe(
            object : SingleObserver<T>, Observer by observer, ErrorCallback by observer {
                override fun onSuccess(value: T) {
                    observer.tryCatch(block = { mapper(value) }, onSuccess = observer::onSuccess)
                }
            }
        )
    }