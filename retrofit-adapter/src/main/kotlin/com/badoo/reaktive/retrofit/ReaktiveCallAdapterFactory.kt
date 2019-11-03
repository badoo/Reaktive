package com.badoo.reaktive.retrofit

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asCompletable
import com.badoo.reaktive.single.asMaybe
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.observeBody
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ReaktiveCallAdapterFactory private constructor(private val scheduler: Scheduler?): CallAdapter.Factory() {

    companion object {

        @JvmStatic
        fun create() = ReaktiveCallAdapterFactory(null)

        @JvmStatic
        fun createWithScheduler(scheduler: Scheduler) = ReaktiveCallAdapterFactory(scheduler)
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {

        val rawType = getRawType(returnType)
        val isBody = returnType.isBodyType()

        return when(rawType) {
            Single::class.java -> callAdapter(returnType.getResponseType()) { call -> call.asSingle(scheduler).transformToBody(isBody) }
            Completable::class.java -> callAdapter(Unit::class.java) { call -> call.asSingle(scheduler).observeBody().asCompletable() }
            Maybe::class.java -> callAdapter(returnType.getResponseType()) { call -> call.asSingle(scheduler).transformToBody(isBody).asMaybe() }
            Observable::class.java -> callAdapter(returnType.getResponseType()) { call -> call.asSingle(scheduler).transformToBody(isBody).asObservable() }
            else -> return null
        }
    }

    private fun Type.getResponseType(): Type {
        check(this is ParameterizedType) { "Return type must be parameterized" }

        val observableType = getParameterUpperBound(0, this)
        return when (getRawType(observableType)) {
            Response::class.java -> {
                check(observableType is ParameterizedType) { "Response must be parameterized as Response<Foo> or Response<? extends Foo>" }
                getParameterUpperBound(0, observableType)
            }
            else -> observableType
        }
    }

    private fun Type.isBodyType(): Boolean {
        return when (this) {
            Completable::class.java -> true
            else -> {
                check(this is ParameterizedType) { "Return type must be parameterized" }
                getRawType(getParameterUpperBound(0, this)) != Response::class.java
            }
        }
    }

    private fun <T> Single<Response<T>>.transformToBody(transform: Boolean): Single<*> =
            if (transform) this.observeBody() else this
}