package com.badoo.reaktive.retrofit

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal inline fun callAdapter(responseType: Type, crossinline adapt: (Call<Any>) -> Any): CallAdapter<Any, Any> =
        object : CallAdapter<Any, Any> {
            override fun responseType(): Type = responseType

            override fun adapt(call: Call<Any>): Any = adapt(call)
        }