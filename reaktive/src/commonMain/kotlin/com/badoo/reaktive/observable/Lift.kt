package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable

typealias Lift<T, R> = (proceed: ObservableObserver<R>) -> ObservableObserver<T>

fun <T, R> Observable<T>.lift(lift: Lift<T, R>): Observable<R> =
    observable { emitter ->
        emitter
            .toObserver()
            .let(lift)
            .let { subscribeSafe(it) }
    }

private fun <T> ObservableEmitter<T>.toObserver() = object : ObservableObserver<T> {

    private val emitter = this@toObserver

    override fun onSubscribe(disposable: Disposable) = emitter.setDisposable(disposable)

    override fun onNext(value: T) {
        try {
            emitter.onNext(value)
        } catch (error: Throwable) {
            emitter.onError(error)
        }
    }

    override fun onComplete() = emitter.onComplete()

    override fun onError(error: Throwable) = emitter.onError(error)

}
