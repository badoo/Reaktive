package com.badoo.reaktive.observable

expect fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T>
