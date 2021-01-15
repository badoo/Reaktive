package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue

@ExperimentalReaktiveApi
actual object ReaktivePlugins {

    internal actual var plugins: MutableList<ReaktivePlugin>? by AtomicReference(null)
}
