package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.completable.completable

fun Maybe<*>.asCompletable(): Completable =
    completable { observer ->
        subscribeSafe(
            object : MaybeObserver<Any?>, CompletableObserver by observer {
                override fun onSuccess(value: Any?) {
                    observer.onComplete()
                }
            }
        )
    }