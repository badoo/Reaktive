package com.badoo.reaktive.retrofit

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import retrofit2.Call
import retrofit2.Response

internal fun <T> Call<T>.asSingle(): Single<Response<T>> = single { emitter ->
    emitter.setDisposable(Disposable(this::cancel))
    emitter.onSuccess(this.execute())
}