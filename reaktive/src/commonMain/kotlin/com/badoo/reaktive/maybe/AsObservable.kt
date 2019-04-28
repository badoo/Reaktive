package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableUnsafe

fun <T> Maybe<T>.asObservable(): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    observer.onNext(value)
                    observer.onComplete()
                }
            }
        )
    }