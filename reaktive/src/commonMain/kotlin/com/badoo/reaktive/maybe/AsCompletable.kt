package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.base.subscribeSafe

fun Maybe<*>.asCompletable(): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<Any?>, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: Any?) {
                    observer.onComplete()
                }
            }
        )
    }