package com.badoo.reaktive.retrofit

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.test.base.TestObserver
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

internal class ReaktiveCallAdapterFactoryTest {

    private val server = MockWebServer()

    internal interface TestApi {

        @GET("/")
        fun completable(): Completable

        @GET("/")
        fun single(): Single<String>

        @GET("/")
        fun observable(): Observable<String>

        @GET("/")
        fun maybe(): Maybe<String>
    }

    private val testApi: TestApi = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addCallAdapterFactory(ReaktiveCallAdapterFactory())
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(TestApi::class.java)

    private val responseBody = "Hi"
    private val errorResponseCode = 404
    private val errorResponseMessage = "HTTP 404 Client Error"

    @Test
    fun `completable completes WHEN server response with success`() {
        server.enqueue(MockResponse().setBody(responseBody))
        testApi.completable()
            .test()
            .assertComplete()
    }

    @Test
    fun `single returns value WHEN server response with success`() {
        server.enqueue(MockResponse().setBody(responseBody))
        testApi.single()
            .test()
            .assertSuccess(responseBody)
    }

    @Test
    fun `observable returns value WHEN server response with success`() {
        server.enqueue(MockResponse().setBody(responseBody))
        testApi.observable()
            .test()
            .assertValue(responseBody)
    }

    @Test
    fun `maybe returns value WHEN server response with success`() {
        server.enqueue(MockResponse().setBody(responseBody))
        testApi.maybe()
            .test()
            .assertSuccess(responseBody)
    }

    @Test
    fun `completable fails WHEN server response with error`() {
        server.enqueue(MockResponse().setResponseCode(errorResponseCode))
        testApi.completable()
            .test()
            .assertError(HttpException::class.java, errorResponseMessage)
    }

    @Test
    fun `single fails WHEN server response with error`() {
        server.enqueue(MockResponse().setResponseCode(errorResponseCode))
        testApi.single()
            .test()
            .assertError(HttpException::class.java, errorResponseMessage)
    }

    @Test
    fun `observable fails WHEN server response with error`() {
        server.enqueue(MockResponse().setResponseCode(errorResponseCode))
        testApi.observable()
            .test()
            .assertError(HttpException::class.java, errorResponseMessage)
    }

    @Test
    fun `maybe fails WHEN server response with error`() {
        server.enqueue(MockResponse().setResponseCode(errorResponseCode))
        testApi.maybe()
            .test()
            .assertError(HttpException::class.java, errorResponseMessage)
    }

    private fun TestObserver.assertError(clazz: Class<out Exception>, message: String) {
        assert(error?.javaClass == clazz)
        assert(error?.localizedMessage == message)
    }
}