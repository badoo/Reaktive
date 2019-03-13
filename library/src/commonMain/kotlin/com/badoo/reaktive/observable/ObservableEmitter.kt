package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableEmitter

interface ObservableEmitter<in T> : CompletableEmitter {

    fun onNext(value: T)
}