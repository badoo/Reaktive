package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.single.SingleObserver

/**
 * Represents [Observer] that acts as both [CompletableObserver] and [SingleObserver]
 */
interface MaybeObserver<in T> : CompletableObserver, SingleObserver<T>