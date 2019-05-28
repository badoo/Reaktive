package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks

fun <T> Maybe<T>.filter(predicate: (T) -> Boolean): Maybe<T> =
    maybeUnsafe { observer ->
        subscribeSafe(
            object : MaybeObserver<T>, Observer by observer, CompletableCallbacks by observer {
                override fun onSuccess(value: T) {
                    observer.tryCatch({ predicate(value) }) {
                        if (it) {
                            observer.onSuccess(value)
                        } else {
                            observer.onComplete()
                        }
                    }
                }
            }
        )
    }