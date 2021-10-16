package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable

/**
 * Calls the [supplier] for each new observer and subscribes to the returned [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#defer-java.util.concurrent.Callable-).
 */
fun <T> observableDefer(supplier: () -> Observable<T>): Observable<T> =
    observable { emitter ->
        supplier().subscribe(
            object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }
            }
        )
    }
