package com.badoo.reaktive.utils

import com.badoo.reaktive.disposable.Disposable

internal open class ThreadLocalDisposableHolder<T>(initialValue: T? = null) : ThreadLocalHolder<T>(initialValue), Disposable
