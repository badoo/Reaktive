package com.badoo.reaktive.test

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

internal actual fun doInBackground(block: () -> Unit) {
    Worker.start(errorReporting = true).execute(TransferMode.SAFE, { block.freeze() }) { it() }
}
