package com.badoo.reaktive.subject.replay

import com.badoo.reaktive.subject.Subject

/**
 * A [Subject] that replays events to current and late observers.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/subjects/ReplaySubject.html).
 *
 * The following factory function is available:
 * - `ReplaySubject<T>()`
 */
interface ReplaySubject<T> : Subject<T>
