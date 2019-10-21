package com.badoo.reaktive.sample.linux

import com.badoo.reaktive.samplemppmodule.KittenStoreBuilderImpl
import com.badoo.reaktive.samplemppmodule.binder.KittenBinder
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.reinterpret
import libgtk3.GDK_WINDOW_STATE_ICONIFIED
import libgtk3.GdkEventWindowState
import libgtk3.GtkApplication
import libgtk3.GtkWindow
import libgtk3.gtk_application_window_new
import libgtk3.gtk_container_set_border_width
import libgtk3.gtk_widget_show_all
import libgtk3.gtk_window_set_default_size
import libgtk3.gtk_window_set_resizable
import libgtk3.gtk_window_set_title

class MainWindow(app: CPointer<GtkApplication>) {

    private val window: CPointer<GtkWindow> = gtk_application_window_new(app).requireNotNull().reinterpret()
    private val view: KittenViewImpl = KittenViewImpl(window)
    private val binder = KittenBinder(KittenStoreBuilderImpl())

    private var isMinimized = false

    init {
        gtk_window_set_title(window, "Kittens")
        gtk_window_set_default_size(window, WINDOW_WIDTH, WINDOW_HEIGHT)
        gtk_window_set_resizable(window, 0)
        gtk_container_set_border_width(window.reinterpret(), WINDOW_BORDER_WIDTH.toUInt())

        window.signalConnect1("window-state-event", ::onWindowStateChanged)
        window.signalConnect0("destroy", ::onDestroy)
    }

    fun show() {
        gtk_widget_show_all(window.reinterpret())
        binder.onViewCreated(view)
        binder.onStart()
    }

    private fun onWindowStateChanged(state: GdkEventWindowState) {
        val isMinimized = state.new_window_state and GDK_WINDOW_STATE_ICONIFIED > 0u
        if (isMinimized != this.isMinimized) {
            this.isMinimized = isMinimized
            if (isMinimized) {
                binder.onStop()
            } else {
                binder.onStart()
            }
        }
    }

    private fun onDestroy() {
        if (!isMinimized) {
            binder.onStop()
        }
        binder.onViewDestroyed()
        binder.onDestroy()
    }

    private companion object {
        private const val WINDOW_WIDTH = 400
        private const val WINDOW_HEIGHT = 300
        private const val WINDOW_BORDER_WIDTH = 8
    }
}
