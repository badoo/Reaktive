package com.badoo.reaktive.testutils

val TestObservable<*>.hasSubscribers: Boolean get() = observers.isNotEmpty()