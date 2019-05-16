package com.badoo.reaktive.utils.queue

internal actual class ArrayQueue<T> actual constructor() : Queue<T> by QueueImpl(java.util.ArrayDeque())