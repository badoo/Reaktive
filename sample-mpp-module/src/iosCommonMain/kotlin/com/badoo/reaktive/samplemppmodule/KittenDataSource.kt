package com.badoo.reaktive.samplemppmodule

interface KittenDataSource {

    fun load(url: String, continuation: (String?, Throwable?) -> Unit)
}