package com.badoo.reaktive.promise

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.singleByEmitter
import kotlin.js.Promise

fun <T> Promise<T>.toReaktive(): Single<T> = singleByEmitter { emitter ->
    then(onFulfilled = emitter::onSuccess, onRejected = emitter::onError)
}
