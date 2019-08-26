package com.badoo.reaktive.samplemppmodule

interface KittenDataSource {

    fun load(url: String, continuation: (Result) -> Unit)

    sealed class Result {
        class Success(val data: String) : Result()
        class Failure(val throwable: Throwable) : Result()
    }
}