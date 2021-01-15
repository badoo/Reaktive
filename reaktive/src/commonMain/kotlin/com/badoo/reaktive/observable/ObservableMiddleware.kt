package com.badoo.reaktive.observable

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.utils.SharedList

@ExperimentalReaktiveApi
object ObservableMiddleware {

    private val list = SharedList<Middleware>()

    fun register(middleware: Middleware) {
        list += middleware
    }

    fun unregister(middleware: Middleware) {
        list -= middleware
    }

    fun <T> wrap(observable: Observable<T>): Observable<T> =
        if (list.isEmpty()) {
            observable
        } else {
            list.fold(observable) { obs, middleware -> middleware.wrap(obs) }
        }

    interface Middleware {
        fun <T> wrap(observable: Observable<T>): Observable<T>
    }
}
