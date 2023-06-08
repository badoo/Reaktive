package com.badoo.reaktive.subject.unicast

/**
 * Create a new instance of [UnicastSubject].
 *
 * @param bufferSize the maximum number of elements that the returned [UnicastSubject] will store and replay,
 * default value is [Int.MAX_VALUE]
 * @param onTerminate called when the returned [UnicastSubject] receives a terminal event (`onComplete` or `onError`)
 */
@Suppress("FunctionName")
fun <T> UnicastSubject(bufferSize: Int = Int.MAX_VALUE, onTerminate: () -> Unit = {}): UnicastSubject<T> {
    require(bufferSize > 0) { "Buffer size must be a positive value" }

    return UnicastSubjectImpl(
        bufferLimit = bufferSize,
        onTerminate = onTerminate,
    )
}
