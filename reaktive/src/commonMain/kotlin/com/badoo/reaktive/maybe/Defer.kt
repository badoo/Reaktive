package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable

/**
 * Calls the [supplier] for each new observer and subscribes to the returned [Maybe].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#defer-java.util.concurrent.Callable-).
 */
fun <T> maybeDefer(supplier: () -> Maybe<T>): Maybe<T> =
    maybe { emitter ->
        supplier().subscribe(
            object : MaybeObserver<T>, MaybeCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
