package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.observable.Observable

interface BehaviorObservable<out T> : Observable<T> {

    val value: T
}
