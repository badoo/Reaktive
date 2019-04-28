package com.badoo.reaktive.looperthread

internal class UnboundLooperThreadStrategy : LooperThreadStrategy {

    override fun get(): LooperThread = LooperThread()

    override fun recycle(looperThread: LooperThread) {
        looperThread.destroy() // TODO: Add caching with timeout
    }

    override fun destroy() {
        // no-op
    }
}