package com.badoo.reaktive.utils.timer.external

external fun setTimeout(handler: dynamic, timeout: Int, vararg arguments: Any?): Int
external fun setInterval(handler: dynamic, timeout: Int, vararg arguments: Any?): Int
external fun clearTimeout(handle: Int = definedExternally)
external fun clearInterval(handle: Int = definedExternally)
