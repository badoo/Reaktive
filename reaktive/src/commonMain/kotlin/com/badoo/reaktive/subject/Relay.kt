package com.badoo.reaktive.subject

import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.observable.Observable

interface Relay<T> : Observable<T>, ValueCallback<T>