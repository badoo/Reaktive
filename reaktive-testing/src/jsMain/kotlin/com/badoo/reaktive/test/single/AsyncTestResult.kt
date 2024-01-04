package com.badoo.reaktive.test.single

@Suppress("UnusedPrivateMember")
@JsName("Promise")
actual external class AsyncTestResult(
    executor: (resolve: (Unit) -> Unit, reject: (Throwable) -> Unit) -> Unit,
)
