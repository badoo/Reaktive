package com.badoo.reaktive.subject.unicast

import com.badoo.reaktive.subject.Subject

/**
 * A [Subject] that queues up events until a single observer subscribes to it, replays those events to it
 * until the observer catches up and then switches to relaying events live to this single observer until this
 * [UnicastSubject] terminates or the observer unsubscribes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/subjects/UnicastSubject.html).
 *
 * The following factory function is available:
 * - `UnicastSubject<T>()`
 */
interface UnicastSubject<T> : Subject<T>
