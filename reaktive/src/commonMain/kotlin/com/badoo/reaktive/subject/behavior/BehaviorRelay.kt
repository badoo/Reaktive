package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.subject.Relay

/**
 * Represents a [Relay] and [BehaviorObservable] at the same time. It is same as [BehaviorSubject],
 * but without ability to send `onComplete` and `onError` signals.
 *
 * See [Relay] and [BehaviorObservable] for more information.
 */
interface BehaviorRelay<T> : Relay<T>, BehaviorObservable<T>
