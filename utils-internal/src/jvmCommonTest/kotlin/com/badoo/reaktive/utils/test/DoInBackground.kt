@file:JvmName("DoInBackgroundJvm")

package com.badoo.reaktive.utils.test

import kotlin.jvm.JvmName

actual fun doInBackground(block: () -> Unit) {
    Thread(block).start()
}
