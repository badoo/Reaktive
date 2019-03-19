package com.badoo.reaktive.single

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.maybe.maybe
import com.badoo.reaktive.maybe.subscribeSafe

fun <T, R> Single<T>.flatMapMaybe(mapper: (T) -> Maybe<R>): Maybe<R> =
    maybe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T>, Observer by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    try {
                        mapper(value)
                    } catch (e: Throwable) {
                        onError(e)
                        return
                    }
                        .subscribeSafe(
                            object : MaybeObserver<R> by observer {
                                override fun onSubscribe(disposable: Disposable) {
                                    disposableWrapper.set(disposable)
                                }
                            }
                        )
                }
            }
        )
    }