package com.badoo.reaktive.test

import com.badoo.reaktive.base.ErrorCallback

class TestErrorCallback(
    private val onError: (Throwable) -> Unit = {},
) : ErrorCallback {

    var error: Throwable? = null

    override fun onError(error: Throwable) {
        onError.invoke(error)
        this.error = error
    }
}
