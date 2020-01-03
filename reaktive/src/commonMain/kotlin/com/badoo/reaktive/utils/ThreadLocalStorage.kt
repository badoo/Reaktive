package com.badoo.reaktive.utils

import com.badoo.reaktive.disposable.Disposable

@Deprecated(
    message = "Use ThreadLocalHolder from 'utils' package. This class will be removed soon.",
    replaceWith = ReplaceWith("ThreadLocalHolder", "com.badoo.reaktive.utils.ThreadLocalHolder"),
    level = DeprecationLevel.WARNING
)
open class ThreadLocalStorage<T : Any>(initialValue: T? = null) : ThreadLocalHolder<T>(initialValue), Disposable
