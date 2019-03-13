package com.arkivanov.rxkotlin.maybe

interface Maybe<out T> {

    fun subscribe(observer: MaybeObserver<T>)
}