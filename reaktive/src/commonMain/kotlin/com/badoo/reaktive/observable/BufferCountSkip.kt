package com.badoo.reaktive.observable

/**
 * Returns an [Observable] that emits buffered [List]s of elements it collects from the source [Observable].
 * The first buffer is started with the first element emitted by the source [Observable].
 * Every subsequent buffer is started every [skip] elements, making overlapping buffers possible.
 * Buffers are emitted once the size reaches [count] elements.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#buffer-int-int-).
 */
fun <T> Observable<T>.buffer(count: Int, skip: Int = count): Observable<List<T>> =
    window(count = count.toLong(), skip = skip.toLong())
        .flatMapSingle { it.toList() }
