package com.badoo.reaktive.subject.behavior

import com.badoo.reaktive.subject.Relay

interface BehaviorRelay<T> : Relay<T>, BehaviorObservable<T>
