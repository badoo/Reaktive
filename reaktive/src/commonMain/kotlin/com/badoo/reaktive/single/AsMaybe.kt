package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

fun <T> Single<T>.asMaybe(): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : SingleObserver<T>, SuccessCallback<T> by emitter, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
