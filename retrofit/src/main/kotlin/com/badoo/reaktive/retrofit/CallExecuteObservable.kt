package com.badoo.reaktive.retrofit

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import retrofit2.Call
import retrofit2.Response

internal class CallExecuteObservable<T>(private val originalCall: Call<T>) : Observable<Response<T>> {

    override fun subscribe(observer: ObservableObserver<Response<T>>) {
        val call = originalCall.clone()
        val disposable = CallDisposable(call)
        observer.onSubscribe(disposable)
        if (disposable.isDisposed)
            return

        var terminated = false
        try {
            val response = call.execute()
            if (disposable.isDisposed.not())
                observer.onNext(response)

            if (disposable.isDisposed.not()) {
                terminated = true
                observer.onComplete()
            }

        } catch (t: Throwable) {

            if (terminated.not() && disposable.isDisposed.not())
                observer.onError(t)

        }
    }

    private class CallDisposable internal constructor(private val call: Call<*>) : Disposable {

        @Volatile
        override var isDisposed: Boolean = false
            private set

        override fun dispose() {
            isDisposed = true
            call.cancel()
        }
    }
}