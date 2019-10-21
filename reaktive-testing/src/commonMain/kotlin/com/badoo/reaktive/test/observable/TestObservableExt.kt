package com.badoo.reaktive.test.observable

fun <T> TestObservable<T>.onNext(vararg values: T) {
    values.forEach(::onNext)
}
