package com.badoo.reaktive.utils.queue

internal actual class PriorityQueue<T> actual constructor(
    comparator: Comparator<in T>
) : Queue<T> by QueueImpl(java.util.PriorityQueue(11, comparator))
