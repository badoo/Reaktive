package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Connectable

interface ConnectableObservable<T> : Observable<T>, Connectable
