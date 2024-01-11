package com.badoo.reaktive.test.single

@Suppress("UnusedPrivateMember")
@JsName("Promise")
actual external class AsyncTestResult(
    executor: (resolve: (JsAny) -> Unit, reject: (JsAny) -> Unit) -> Unit,
)
