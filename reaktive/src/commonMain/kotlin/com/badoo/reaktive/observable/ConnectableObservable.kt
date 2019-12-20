package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Connectable

interface ConnectableObservable<out T> : Observable<T>, Connectable
