package com.badoo.reaktive.observable

import com.badoo.reaktive.base.operator.publish
import com.badoo.reaktive.subject.publish.publishSubject

fun <T> Observable<T>.publish(): ConnectableObservable<T> = publish(::publishSubject)