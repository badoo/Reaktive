package com.badoo.reaktive.utils

import kotlin.native.concurrent.freeze

internal actual fun <T> T.freeze(): T = freeze()