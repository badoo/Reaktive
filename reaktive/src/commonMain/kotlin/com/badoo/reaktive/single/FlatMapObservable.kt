package com.badoo.reaktive.single

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observable

fun <T, R> Single<T>.flatMapObservable(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        subscribe(
            object : SingleObserver<T>, ObservableObserver<R>, ObservableCallbacks<R> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch { mapper(value).subscribe(this) }
                }
            }
        )
    }

fun <T, U, R> Single<T>.flatMapObservable(mapper: (T) -> Observable<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapObservable { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
