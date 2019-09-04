@file:JvmName("DoInBackgroundJvm")

package com.badoo.reaktive.test

import kotlin.jvm.JvmName

internal actual fun doInBackground(block: () -> Unit) {
    Thread(block).start()
}