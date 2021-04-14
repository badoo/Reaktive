package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.utils.SharedList
import kotlin.jvm.JvmField

@ExperimentalReaktiveApi
@JvmField
internal actual var plugins: SharedList<ReaktivePlugin>? = null
