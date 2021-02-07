package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.utils.SharedList
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.freeze

@ExperimentalReaktiveApi
@SharedImmutable
private val _plugins = AtomicReference<SharedList<ReaktivePlugin>?>(null)

@ExperimentalReaktiveApi
internal actual var plugins: SharedList<ReaktivePlugin>?
    get() = _plugins.value
    set(value) {
        _plugins.value = value.freeze()
    }
