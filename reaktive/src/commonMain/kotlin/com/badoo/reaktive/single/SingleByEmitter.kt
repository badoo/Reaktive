package com.badoo.reaktive.single

// Separate implementations are because JS tests are randomly failing with ReferenceError if the function is inlined.
// So do not inline for JS at the moment.
@Suppress("ForbiddenComment")
// TODO: recheck later
expect fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T>
