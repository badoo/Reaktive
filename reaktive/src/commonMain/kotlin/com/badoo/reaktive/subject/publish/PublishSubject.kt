package com.badoo.reaktive.subject.publish

import com.badoo.reaktive.subject.Subject

/**
 * A [Subject] that multicasts elements to all currently subscribed observers.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/subjects/PublishSubject.html).
 *
 * The following factory function is available:
 * - `PublishSubject<T>()`
 */
interface PublishSubject<T> : Subject<T>
