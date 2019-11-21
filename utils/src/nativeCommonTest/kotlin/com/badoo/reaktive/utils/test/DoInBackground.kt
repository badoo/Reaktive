package com.badoo.reaktive.utils.test

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

actual fun doInBackground(block: () -> Unit) {
    Worker.start(errorReporting = true).execute(TransferMode.SAFE, { block.freeze() }) { it() }
}
