package com.badoo.reaktive.test.base

val TestSource<*>.hasSubscribers: Boolean get() = observers.isNotEmpty()