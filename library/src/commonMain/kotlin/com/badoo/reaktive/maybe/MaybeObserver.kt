package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.CompletableObserver

interface MaybeObserver<in T> : CompletableObserver {

    fun onSuccess(value: T)
}