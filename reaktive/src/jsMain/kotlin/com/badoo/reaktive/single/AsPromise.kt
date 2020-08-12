package com.badoo.reaktive.single

import kotlin.js.Promise

fun <T> Single<T>.asPromise(): Promise<T> =
    Promise { resolve, reject ->
        subscribe(onSuccess = resolve, onError = reject)
    }
