package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.samplemppmodule.store.KittenStore
import com.badoo.reaktive.samplemppmodule.store.KittenStoreBuilder
import com.badoo.reaktive.samplemppmodule.store.KittenStoreImpl

class KittenStoreBuilderImpl(
    private val dataSource: KittenDataSource
) : KittenStoreBuilder {

    override fun build(): KittenStore = KittenStoreImpl(loader = KittenLoaderImpl(dataSource))
}
