package com.badoo.reaktive.sample.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.badoo.reaktive.samplemppmodule.binder.KittenBinder
import com.badoo.reaktive.samplemppmodule.KittenStoreBuilderImpl

class MainActivity : AppCompatActivity() {

    private lateinit var kittenBinder: KittenBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        kittenBinder = KittenBinder(KittenStoreBuilderImpl())
        kittenBinder.onViewCreated(KittenViewImpl(findViewById(android.R.id.content)))
    }

    override fun onStart() {
        super.onStart()

        kittenBinder.onStart()
    }

    override fun onStop() {
        kittenBinder.onStop()

        super.onStop()
    }

    override fun onDestroy() {
        kittenBinder.onDestroy()

        super.onDestroy()
    }
}
