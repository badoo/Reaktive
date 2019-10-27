package com.badoo.reaktive.retrofit

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.firstOrError
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class ReaktiveCallAdapter(
        private val responseType: Type,
        private val isBody: Boolean,
        private val isSingle: Boolean,
        private val isMaybe: Boolean,
        private val isCompletable: Boolean
) : CallAdapter<Any, Any> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<Any>): Any {
        val responseObservable = CallExecuteObservable(call)

        val observable: Observable<*> = if (isBody) BodyObservable(responseObservable)
        else responseObservable

        return when {
            isSingle -> observable.firstOrError()
            isMaybe -> observable.firstOrComplete()
            isCompletable -> observable.asCompletable()
            else -> observable
        }
    }
}