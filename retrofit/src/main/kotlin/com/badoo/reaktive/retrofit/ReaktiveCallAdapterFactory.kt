package com.badoo.reaktive.retrofit

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ReaktiveCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)

        if (rawType == Completable::class.java)
            return ReaktiveCallAdapter(
                    responseType = Unit::class.java,
                    isBody = true,
                    isSingle = false,
                    isMaybe = false,
                    isCompletable = true
            )

        val isSingle = rawType == Single::class.java
        val isMaybe = rawType == Maybe::class.java

        if (rawType != Observable::class.java && !isSingle && !isMaybe)
            return null

        var isBody = false
        val responseType: Type
        if (returnType !is ParameterizedType) {
            val name = when {
                isSingle -> "Single"
                isMaybe -> "Maybe"
                else -> "Observable"
            }
            throw IllegalStateException("$name return type must be parameterized as $name<Foo> or $name<? extends Foo>")
        }

        val observableType = getParameterUpperBound(0, returnType)
        when (getRawType(observableType)) {
            Response::class.java -> {
                check(observableType is ParameterizedType) { "Response must be parameterized as Response<Foo> or Response<? extends Foo>" }
                responseType = getParameterUpperBound(0, observableType)
            }
            else -> {
                responseType = observableType
                isBody = true
            }
        }

        return ReaktiveCallAdapter(responseType, isBody, isSingle, isMaybe, false)
    }
}
