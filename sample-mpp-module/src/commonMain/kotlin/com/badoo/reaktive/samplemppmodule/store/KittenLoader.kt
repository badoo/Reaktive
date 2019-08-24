package com.badoo.reaktive.samplemppmodule.store

import com.badoo.reaktive.annotations.EventsOnAnyScheduler
import com.badoo.reaktive.single.Single

interface KittenLoader {

    @EventsOnAnyScheduler
    fun load(): Single<Result>

    sealed class Result {
        class Success(val json: String) : Result()
        object Error : Result()
    }
}