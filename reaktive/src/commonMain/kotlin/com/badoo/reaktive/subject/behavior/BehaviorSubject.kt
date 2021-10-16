package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.subject.Subject

/**
 * A [Subject] that emits the most recent element it has observed and all subsequent observed items to each subscribed observer.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/subjects/BehaviorSubject.html).
 *
 * The following factory function is available:
 * - `BehaviorSubject<T>(initialValue: T)`
 */
interface BehaviorSubject<T> : Subject<T>, BehaviorRelay<T>
