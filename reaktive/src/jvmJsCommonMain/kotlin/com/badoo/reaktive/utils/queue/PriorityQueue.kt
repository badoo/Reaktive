package com.badoo.reaktive.utils.queue

expect class PriorityQueue<T>(comparator: Comparator<in T>) : Queue<T>
