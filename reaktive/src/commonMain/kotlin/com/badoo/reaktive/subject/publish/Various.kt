package com.badoo.reaktive.subject.publish

@Deprecated(
    message = "Use PublishSubject() builder",
    replaceWith = ReplaceWith(
        "PublishSubject<T>()",
        "com.badoo.reaktive.subject.PublishSubject"
    ),
    level = DeprecationLevel.WARNING
)
fun <T> publishSubject(): PublishSubject<T> = PublishSubject()
