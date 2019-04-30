package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable

typealias Route<T, R> = (proceed: (T) -> Unit) -> (R) -> Unit

fun <T, R> Observable<T>.route(route: Route<R, T>): Observable<R> =
    lift { observer: ObservableObserver<R> -> observer.compose(route) }

private fun <T, R> ObservableObserver<T>.compose(route: Route<T, R>) = object : ObservableObserver<R> {

    private val observer = this@compose
    private val onNext = route(observer::onNext)

    override fun onSubscribe(disposable: Disposable) = observer.onSubscribe(disposable)

    override fun onNext(value: R) = onNext.invoke(value)

    override fun onComplete() = observer.onComplete()

    override fun onError(error: Throwable) = observer.onError(error)

}
