package com.badoo.reaktive.test

import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker

@OptIn(ObsoleteWorkersApi::class) // No replacement yet
internal actual fun doInBackground(block: () -> Unit) {
    Worker.start(errorReporting = true).execute(TransferMode.SAFE, { block }) { it() }
}
