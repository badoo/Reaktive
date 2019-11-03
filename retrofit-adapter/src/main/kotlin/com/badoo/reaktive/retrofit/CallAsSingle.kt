package com.badoo.reaktive.retrofit

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import com.badoo.reaktive.single.subscribeOn
import retrofit2.Call
import retrofit2.Response

internal fun <T> Call<T>.asSingle(scheduler: Scheduler?): Single<Response<T>> {
    val resultSingle: Single<Response<T>> = single { emitter ->
        emitter.setDisposable(Disposable(this::cancel))
        emitter.onSuccess(this.execute())
    }
    scheduler?.run { resultSingle.subscribeOn(this) }
    return resultSingle
}