package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import com.badoo.reaktive.utils.ObjectReference

fun <T, C> Observable<T>.collect(initialCollection: C, accumulator: (C, T) -> C): Single<C> =
    single { emitter ->
        subscribe(
            object : ObjectReference<C>(initialCollection), ObservableObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    emitter.tryCatch {
                        this.value = accumulator(this.value, value)
                    }
                }

                override fun onComplete() {
                    emitter.onSuccess(value)
                }
            }
        )
    }
