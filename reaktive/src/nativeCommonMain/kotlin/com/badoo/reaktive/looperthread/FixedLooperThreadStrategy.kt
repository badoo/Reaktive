package com.badoo.reaktive.looperthread

import kotlin.concurrent.AtomicInt

internal class FixedLooperThreadStrategy(threadCount: Int) : LooperThreadStrategy {

    private val pool = List(threadCount) { LooperThread() }
    private val threadIndex = AtomicInt(-1)

    override fun get(): LooperThread = pool[threadIndex.addAndGet(1) % pool.size]

    override fun recycle(looperThread: LooperThread) {
        // no-op
    }

    override fun destroy() {
        pool.forEach(LooperThread::destroy)
    }
}
