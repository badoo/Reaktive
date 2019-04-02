package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.subject.Subject

interface BehaviorSubject<T> : Subject<T> {

    val value: T
}