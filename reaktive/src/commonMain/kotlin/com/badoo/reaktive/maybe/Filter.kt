package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Filters the value emitted by the [Maybe] using the provided [predicate].
 * The returned [Maybe] signals `onSuccess` if the [predicate] returned `true`, otherwise signals `onComplete`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#filter-io.reactivex.functions.Predicate-).
 */
fun <T> Maybe<T>.filter(predicate: (T) -> Boolean): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
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
