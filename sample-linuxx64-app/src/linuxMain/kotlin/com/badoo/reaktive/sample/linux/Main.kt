package com.badoo.reaktive.sample.linux

import com.badoo.reaktive.scheduler.overrideSchedulers
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.reinterpret
import libgtk3.G_APPLICATION_FLAGS_NONE
import libgtk3.GtkApplication
import libgtk3.g_application_run
import libgtk3.g_object_unref
import libgtk3.gtk_application_new

/**
 * How to run:
 * * Install libcurl4-openssl-dev and libgtk-3-dev in your system
 * * Execute ":sample-linuxx64-app:runDebugExecutableLinux" Gradle task
 */
fun main() {
    overrideSchedulers(main = ::MainScheduler)

    val app: CPointer<GtkApplication> = gtk_application_new("com.badoo.reaktive.sample.linux", G_APPLICATION_FLAGS_NONE).requireNotNull()

    app.signalConnect("activate") {
        MainWindow(app).show()
    }

    g_application_run(app.reinterpret(), 0, null)
    g_object_unref(app)
}