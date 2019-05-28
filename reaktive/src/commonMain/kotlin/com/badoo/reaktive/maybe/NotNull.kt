package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.completable.CompletableCallbacks

fun <T : Any> Maybe<T?>.notNull(): Maybe<T> =
    maybeUnsafe { observer ->
        subscribeSafe(
            object : MaybeObserver<T?>, Observer by observer, CompletableCallbacks by observer {
                override fun onSuccess(value: T?) {
                    if (value != null) {
                        observer.onSuccess(value)
                    } else {
                        observer.onComplete()
                    }
                }
            }
        )
    }