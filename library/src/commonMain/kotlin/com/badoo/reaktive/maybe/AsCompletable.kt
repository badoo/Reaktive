package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completable

fun Maybe<*>.asCompletable(): Completable =
    completable { observer ->
        subscribeSafe(
            object : MaybeObserver<Any?>, Observer by observer, CompletableCallbacks by observer {
                override fun onSuccess(value: Any?) {
                    observer.onComplete()
                }
            }
        )
    }