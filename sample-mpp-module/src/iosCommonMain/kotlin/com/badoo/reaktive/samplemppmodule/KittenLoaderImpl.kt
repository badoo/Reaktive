package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.samplemppmodule.store.KittenLoader
import com.badoo.reaktive.samplemppmodule.store.KittenLoader.Result
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.onErrorReturnValue
import com.badoo.reaktive.single.single

internal class KittenLoaderImpl(
    private val dataSource: KittenDataSource
) : KittenLoader {

    override fun load(): Single<Result> =
        single<String> { emitter ->
            dataSource.load(Config.KITTEN_URL) { result ->
                when (result) {
                    is KittenDataSource.Result.Success -> emitter.onSuccess(result.data)
                    is KittenDataSource.Result.Failure -> emitter.onError(result.throwable)
                }
            }
        }
            .map(Result::Success)
            .onErrorReturnValue(Result.Error)
}
