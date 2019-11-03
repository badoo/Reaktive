package com.badoo.reaktive.looperthread

internal object UnboundLooperThreadStrategy : LooperThreadStrategy {

    override fun get(): LooperThread = LooperThread()

    override fun recycle(looperThread: LooperThread) {
        looperThread.destroy()
    }

    override fun destroy() {
        // no-op
    }
}
