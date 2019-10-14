package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single

expect fun <T> Observable<T>.toList(): Single<List<T>>
