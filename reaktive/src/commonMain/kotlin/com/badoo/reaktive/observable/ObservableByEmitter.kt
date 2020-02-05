package com.badoo.reaktive.observable

// Separate implementations are because JS tests are randomly failing with ReferenceError if the function is inlined.
// So do not inline for JS at the moment.
@Suppress("ForbiddenComment")
// TODO: recheck later
expect fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T>
