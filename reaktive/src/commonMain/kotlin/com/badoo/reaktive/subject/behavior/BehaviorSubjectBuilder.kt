package com.badoo.reaktive.subject.behavior

/**
 * Creates a new instance of [BehaviorSubject].
 *
 * @param initialValue an initial value of the returned [BehaviorSubject]
 */
@Suppress("FunctionName")
fun <T> BehaviorSubject(initialValue: T): BehaviorSubject<T> =
    BehaviorSubjectImpl(initialValue)
