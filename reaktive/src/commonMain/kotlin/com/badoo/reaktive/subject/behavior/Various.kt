package com.badoo.reaktive.subject.behavior

@Deprecated(
    message = "Use BehaviorSubject() builder",
    replaceWith = ReplaceWith(
        "BehaviorSubject<T>(initialValue)",
        "com.badoo.reaktive.subject.BehaviorSubject"
    ),
    level = DeprecationLevel.WARNING
)
fun <T> behaviorSubject(initialValue: T): BehaviorSubject<T> = BehaviorSubject(initialValue = initialValue)
