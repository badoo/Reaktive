package com.badoo.reaktive.timer.external

external fun setTimeout(handler: dynamic, timeout: Int, vararg arguments: Any?): Int
external fun setInterval(handler: dynamic, timeout: Int, vararg arguments: Any?): Int
external fun clearTimeout(handle: Int)
external fun clearInterval(handle: Int)
