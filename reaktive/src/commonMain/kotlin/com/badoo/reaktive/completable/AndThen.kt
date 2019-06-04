package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun Completable.andThen(completable: Completable): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, Observer, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    completable.subscribeSafe(
                        object : CompletableObserver by observer {
                            override fun onSubscribe(disposable: Disposable) {
                                disposableWrapper.set(disposable)
                            }
                        }
                    )
                }
            }
        )
    }