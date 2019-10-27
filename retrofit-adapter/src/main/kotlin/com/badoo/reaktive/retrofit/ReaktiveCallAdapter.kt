package com.badoo.reaktive.retrofit

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asCompletable
import com.badoo.reaktive.single.asMaybe
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.observeBody
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
        val responseSingle = call.asSingle()

        val single: Single<*> = if (isBody) responseSingle.observeBody()
        else responseSingle

        return when {
            isSingle -> single
            isMaybe -> single.asMaybe()
            isCompletable -> single.asCompletable()
            else -> single.asObservable()
        }
    }
}