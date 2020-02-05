package com.badoo.reaktive.maybe

// Separate implementations are because JS tests are randomly failing with ReferenceError if the function is inlined.
// So do not inline for JS at the moment.
@Suppress("ForbiddenComment")
// TODO: recheck later
expect fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T>
