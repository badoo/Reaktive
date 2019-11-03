package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T> Observable<T>.firstOrComplete(): Maybe<T> =
    maybe { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    emitter.onSuccess(value)
                    disposableWrapper.dispose()
                }
            }
        )
    }
