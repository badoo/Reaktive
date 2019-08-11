package com.badoo.reaktive.samplemppmodule

class SingleLifeEvent<out T : Any>(value: T) {

    private var value: T? = value

    fun handle(): T? = value.also { value = null }
}