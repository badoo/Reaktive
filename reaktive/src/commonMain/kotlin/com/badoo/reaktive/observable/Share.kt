package com.badoo.reaktive.observable

fun <T> Observable<T>.share(): Observable<T> = publish().refCount()