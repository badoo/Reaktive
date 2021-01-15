package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi

@ExperimentalReaktiveApi
expect object ReaktivePlugins {

    internal var plugins: MutableList<ReaktivePlugin>?
}
