package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import kotlin.jvm.JvmField

@ExperimentalReaktiveApi
actual object ReaktivePlugins {

    @JvmField
    internal actual var plugins: MutableList<ReaktivePlugin>? = null
}
