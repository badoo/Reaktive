package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

/**
 * Filters the value emitted by the [Single] using the provided [predicate].
 * The returned [Maybe] signals `onSuccess` if the [predicate] returned `true`, otherwise signals `onComplete`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#filter-io.reactivex.functions.Predicate-).
 */
fun <T> Single<T>.filter(predicate: (T) -> Boolean): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : SingleObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch(block = { predicate(value) }) {
                        if (it) {
                            emitter.onSuccess(value)
                        } else {
                            emitter.onComplete()
                        }
                    }
                }
            }
        )
    }
