package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.subject.Subject

/**
 * The following factory function is available:
 * - `BehaviorSubject<T>(initialValue: T)`
 */
interface BehaviorSubject<T> : Subject<T>, BehaviorRelay<T>
