package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable

/**
 * Calls the [supplier] for each new observer and subscribes to the returned [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#defer-java.util.concurrent.Callable-).
 */
fun completableDefer(supplier: () -> Completable): Completable =
    completable { emitter ->
        supplier().subscribe(
            object : CompletableObserver, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
