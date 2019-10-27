package com.badoo.reaktive.retrofit

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import retrofit2.HttpException
import retrofit2.Response

internal class BodyObservable<T>(private val upstream: Observable<Response<T>>) : Observable<T?> {

    override fun subscribe(observer: ObservableObserver<T?>) {
        upstream.subscribe(BodyObserver(observer))
    }

    private class BodyObserver<R> constructor(
            private val observer: ObservableObserver<R?>
    ) : ObservableObserver<Response<R>> {

        private var terminated = false

        override fun onSubscribe(disposable: Disposable) {
            observer.onSubscribe(disposable)
        }

        override fun onNext(value: Response<R>) {
            if (value.isSuccessful) {
                observer.onNext(value.body())
            } else {
                terminated = true
                observer.onError(HttpException(value))
            }
        }

        override fun onComplete() {
            if (!terminated) observer.onComplete()
        }

        override fun onError(error: Throwable) {
            if (!terminated) observer.onError(error)
        }
    }
}