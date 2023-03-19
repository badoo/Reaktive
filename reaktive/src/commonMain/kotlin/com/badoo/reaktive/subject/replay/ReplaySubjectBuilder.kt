package com.badoo.reaktive.subject.replay

/**
 * Creates a new instance of [ReplaySubject].
 *
 * @param bufferSize the maximum number of elements that the returned [ReplaySubject] will store and replay,
 * default value is [Int.MAX_VALUE]
 */
@Suppress("FunctionName")
fun <T> ReplaySubject(bufferSize: Int = Int.MAX_VALUE): ReplaySubject<T> {
    require(bufferSize > 0) { "Buffer size must be a positive value" }

    return ReplaySubjectImpl(bufferLimit = bufferSize)
}
