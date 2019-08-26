package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.samplemppmodule.store.KittenLoader
import com.badoo.reaktive.samplemppmodule.store.KittenLoader.Result
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.onErrorReturnValue
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn
import kotlinx.cinterop.toKString

internal class KittenLoaderImpl : KittenLoader {

    override fun load(): Single<Result> =
        singleFromFunction { curl(Config.KITTEN_URL) ?: throw IllegalStateException("Unable to download a kitten") }
            .subscribeOn(ioScheduler)
            .map { it.toKString() }
            .map(Result::Success)
            .onErrorReturnValue(Result.Error)
}