package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.promise.asSingle
import com.badoo.reaktive.samplemppmodule.store.KittenLoader
import com.badoo.reaktive.samplemppmodule.store.KittenLoader.Result
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.onErrorReturnValue
import org.w3c.fetch.Response
import kotlin.browser.window
import kotlin.js.Promise

internal class KittenLoaderImpl : KittenLoader {

    override fun load(): Single<Result> =
        window
            .fetch(Config.KITTEN_URL)
            .asSingle()
            .map(Response::text)
            .flatMap(Promise<String>::asSingle)
            .map(Result::Success)
            .onErrorReturnValue(Result.Error)
}