package com.badoo.reaktive.observable.connectable

import com.badoo.reaktive.base.Connectable
import com.badoo.reaktive.observable.Observable

interface ConnectableObservable<T> : Observable<T>, Connectable