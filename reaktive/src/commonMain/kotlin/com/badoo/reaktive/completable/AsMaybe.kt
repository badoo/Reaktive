package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybeUnsafe
import com.badoo.reaktive.base.subscribeSafe

fun <T> Completable.asMaybe(): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }