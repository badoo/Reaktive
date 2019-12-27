package com.badoo.reaktive.samplemppmodule.view

import com.badoo.reaktive.samplemppmodule.view.KittenView.ViewModel

class KittenViewMediator(
    private val show: (ViewModel) -> Unit
) : AbstractKittenView() {

    override fun show(model: ViewModel) {
        show.invoke(model)
    }
}
