package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observable

fun <T, R> Single<T>.flatMapObservable(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        subscribeSafe(
            object : SingleObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    val innerObserver =
                        object : ObservableObserver<R>, Observer by this, ObservableCallbacks<R> by emitter {
                        }

                    emitter.tryCatch(block = { mapper(value).subscribe(innerObserver) })
                }
            }
        )
    }

fun <T, U, R> Single<T>.flatMapObservable(mapper: (T) -> Observable<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapObservable { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
