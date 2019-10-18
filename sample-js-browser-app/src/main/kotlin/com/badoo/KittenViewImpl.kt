package com.badoo

import com.badoo.reaktive.samplemppmodule.view.AbstractKittenView
import com.badoo.reaktive.samplemppmodule.view.KittenView.Event
import com.badoo.reaktive.samplemppmodule.view.KittenView.ViewModel
import org.w3c.dom.Image
import kotlin.browser.document
import kotlin.browser.window

class KittenViewImpl : AbstractKittenView() {

    private val loader = requireNotNull(document.getElementById("loader"))
    private val kitten = requireNotNull(document.getElementById("kitten")) as Image

    init {
        document
            .getElementById("load-button")!!
            .addEventListener("click", { dispatch(Event.Reload) })
    }

    override fun show(model: ViewModel) {
        if (model.isError) {
            dispatch(Event.ErrorShown)
            window.alert("Error loading kitten")
        }

        val url = model.kittenUrl
        if (url == null) {
            kitten.setAttribute("hidden", "true")
            kitten.src = ""
        } else {
            kitten.removeAttribute("hidden")
            kitten.src = url
        }

        if (model.isLoading) {
            loader.removeAttribute("hidden")
        } else {
            loader.setAttribute("hidden", "true")
        }
    }
}
