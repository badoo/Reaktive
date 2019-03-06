package com.badoo.reaktive.utils.lock

internal expect fun newLock(): Lock

internal interface Lock {

    fun acquire()

    fun release()
}