package com.badoo.reaktive

import kotlin.js.Promise

@JsExport
external interface Reaktive {
    var name: String
}

@JsExport
fun reaktiveFun(value: String): Promise<dynamic> {
    return Promise.resolve(value)
}

@JsExport
fun String.reaktiveExtensionFun(value: String): Promise<dynamic> {
    return Promise.resolve("$this, $value")
}
