package com.badoo.reaktive.test.observable

val TestObservable<*>.hasSubscribers: Boolean get() = observers.isNotEmpty()