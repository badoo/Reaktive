package com.badoo.reaktive.test

import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker

internal actual fun doInBackground(block: () -> Unit) {
    Worker.start(errorReporting = true).execute(TransferMode.SAFE, { block }) { it() }
}
