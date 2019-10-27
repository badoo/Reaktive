package com.badoo.reaktive.utils.lock

@Suppress("EmptyDefaultConstructor")
expect class Lock constructor() {

    fun acquire()

    fun release()

    fun destroy()

    fun newCondition(): Condition
}
