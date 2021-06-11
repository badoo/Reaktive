package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable

/**
 * Calls the [supplier] for each new observer and subscribes to the returned [Single].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#defer-java.util.concurrent.Callable-).
 */
fun <T> singleDefer(supplier: () -> Single<T>): Single<T> =
    single { emitter ->
        supplier().subscribe(
            object : SingleObserver<T>, SingleCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
