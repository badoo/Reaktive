package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.observable.Observable

/**
 * Represents an [Observable] with a most recent [value], which is automatically emitted to every new observer.
 *
 * See [BehaviorSubject] and [Observable] for more information.
 */
interface BehaviorObservable<out T> : Observable<T> {

    val value: T
}
