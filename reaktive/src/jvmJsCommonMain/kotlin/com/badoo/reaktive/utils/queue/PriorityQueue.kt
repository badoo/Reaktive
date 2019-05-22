package com.badoo.reaktive.utils.queue

internal expect class PriorityQueue<T>(comparator: Comparator<in T>) : Queue<T>