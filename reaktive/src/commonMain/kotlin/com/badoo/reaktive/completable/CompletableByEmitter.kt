package com.badoo.reaktive.completable

// Separate implementations are because JS tests are randomly failing with ReferenceError if the function is inlined.
// So do not inline for JS at the moment.
@Suppress("ForbiddenComment")
// TODO: recheck later
expect fun completable(onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable
