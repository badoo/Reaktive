package com.badoo.reaktive.looperthread

internal interface LooperThreadStrategy {

    fun get(): LooperThread

    fun recycle(looperThread: LooperThread)

    fun destroy()
}