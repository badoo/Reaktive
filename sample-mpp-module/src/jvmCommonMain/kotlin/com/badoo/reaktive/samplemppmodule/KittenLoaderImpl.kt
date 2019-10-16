package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.samplemppmodule.store.KittenLoader
import com.badoo.reaktive.samplemppmodule.store.KittenLoader.Result
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.onErrorReturnValue
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

internal class KittenLoaderImpl : KittenLoader {

    override fun load(): Single<Result> =
        singleFromFunction<Result> {
            val url = URL(Config.KITTEN_URL)
            val connection = url.openConnection() as HttpURLConnection

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Invalid response: ${connection.responseCode}")
            }

            val input = connection.inputStream ?: throw IOException("No input stream")

            Result.Success(input.bufferedReader().readText())
        }
            .subscribeOn(ioScheduler)
            .onErrorReturnValue(Result.Error)
}