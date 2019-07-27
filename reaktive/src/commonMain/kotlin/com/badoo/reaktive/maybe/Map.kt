package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks

fun <T, R> Maybe<T>.map(mapper: (T) -> R): Maybe<R> =
    maybeUnsafe { observer ->
        subscribeSafe(
            object : MaybeObserver<T>, Observer by observer, CompletableCallbacks by observer {
                override fun onSuccess(value: T) {
                    observer.tryCatch(block = { mapper(value) }, onSuccess = observer::onSuccess)
                }
            }
        )
    }
