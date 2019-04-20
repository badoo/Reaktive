package com.badoo.reaktive.promise

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import kotlin.js.Promise

fun <T> Promise<T>.asSingle(): Single<T> =
    single { emitter ->
        then(onFulfilled = emitter::onSuccess, onRejected = emitter::onError)
    }
