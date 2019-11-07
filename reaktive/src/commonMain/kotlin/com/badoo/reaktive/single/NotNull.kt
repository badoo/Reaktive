package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T : Any> Single<T?>.notNull(): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : SingleObserver<T?>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T?) {
                    if (value != null) {
                        emitter.onSuccess(value)
                    } else {
                        emitter.onComplete()
                    }
                }
            }
        )
    }
