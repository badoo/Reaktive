package com.badoo.reaktive.maybe

interface Maybe<out T> {

    fun subscribe(observer: MaybeObserver<T>)
}