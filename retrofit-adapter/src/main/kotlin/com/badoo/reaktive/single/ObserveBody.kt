package com.badoo.reaktive.single

import retrofit2.HttpException
import retrofit2.Response

internal fun <T> Single<Response<T>>.observeBody(): Single<T> = this.flatMap { response ->
    response.takeIf { it.isSuccessful }
        ?.body()
        ?.toSingle()
        ?: HttpException(response).toSingleOfError()
}