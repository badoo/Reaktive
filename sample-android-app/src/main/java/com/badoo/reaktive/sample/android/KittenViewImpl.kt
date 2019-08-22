package com.badoo.reaktive.sample.android

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.badoo.reaktive.samplemppmodule.view.AbstractKittenView
import com.badoo.reaktive.samplemppmodule.view.KittenView.Event
import com.badoo.reaktive.samplemppmodule.view.KittenView.ViewModel
import com.squareup.picasso.Picasso

class KittenViewImpl(private val root: View) : AbstractKittenView() {

    private val image: ImageView = root.findViewById(R.id.image)
    private val progressBar: View = root.findViewById(R.id.progress_bar)

    init {
        root
            .findViewById<View>(R.id.button)
            .setOnClickListener { dispatch(Event.Reload) }
    }

    override fun show(model: ViewModel) {
        if (model.isError) {
            dispatch(Event.ErrorShown)
            Toast.makeText(root.context, R.string.error_loading_kitten, Toast.LENGTH_LONG).show()
        }

        Picasso.get().load(model.kittenUrl).into(image)
        progressBar.visibility = if (model.isLoading) View.VISIBLE else View.INVISIBLE
    }
}