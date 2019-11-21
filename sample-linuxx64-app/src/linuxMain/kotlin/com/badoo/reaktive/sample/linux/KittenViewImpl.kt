package com.badoo.reaktive.sample.linux

import com.badoo.reaktive.samplemppmodule.curl
import com.badoo.reaktive.samplemppmodule.view.AbstractKittenView
import com.badoo.reaktive.samplemppmodule.view.KittenView.Event
import com.badoo.reaktive.samplemppmodule.view.KittenView.ViewModel
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.single.subscribeOn
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import libgtk3.GBytes
import libgtk3.GInputStream
import libgtk3.GTK_DIALOG_DESTROY_WITH_PARENT
import libgtk3.GdkPixbuf
import libgtk3.GtkBox
import libgtk3.GtkButton
import libgtk3.GtkButtonsType
import libgtk3.GtkImage
import libgtk3.GtkMessageType
import libgtk3.GtkOrientation.GTK_ORIENTATION_HORIZONTAL
import libgtk3.GtkOrientation.GTK_ORIENTATION_VERTICAL
import libgtk3.GtkPackType
import libgtk3.GtkSpinner
import libgtk3.GtkWidget
import libgtk3.GtkWindow
import libgtk3.g_bytes_new
import libgtk3.g_memory_input_stream_new_from_bytes
import libgtk3.gdk_pixbuf_new_from_stream_at_scale
import libgtk3.gint
import libgtk3.gtk_box_new
import libgtk3.gtk_box_set_child_packing
import libgtk3.gtk_button_new_with_label
import libgtk3.gtk_container_add
import libgtk3.gtk_image_clear
import libgtk3.gtk_image_new
import libgtk3.gtk_image_set_from_pixbuf
import libgtk3.gtk_message_dialog_new
import libgtk3.gtk_spinner_new
import libgtk3.gtk_spinner_start
import libgtk3.gtk_spinner_stop
import libgtk3.gtk_widget_destroy
import libgtk3.gtk_widget_get_allocated_height
import libgtk3.gtk_widget_get_allocated_width
import libgtk3.gtk_widget_set_size_request
import libgtk3.gtk_widget_show

class KittenViewImpl(
    private val window: CPointer<GtkWindow>
) : AbstractKittenView() {

    private val verticalBox: CPointer<GtkBox> = gtk_box_new(GTK_ORIENTATION_VERTICAL, WIDGET_MARGIN).requireNotNull().reinterpret()
    private val horizontalBox: CPointer<GtkBox> = gtk_box_new(GTK_ORIENTATION_HORIZONTAL, WIDGET_MARGIN).requireNotNull().reinterpret()
    private val button: CPointer<GtkButton> = gtk_button_new_with_label("Load kitten").requireNotNull().reinterpret()
    private val spinner: CPointer<GtkSpinner> = gtk_spinner_new().requireNotNull().reinterpret()
    private val image: CPointer<GtkImage> = gtk_image_new().requireNotNull().reinterpret()

    init {
        gtk_container_add(window.reinterpret(), verticalBox.reinterpret())
        gtk_container_add(verticalBox.reinterpret(), horizontalBox.reinterpret())

        gtk_box_set_child_packing(
            box = verticalBox,
            child = horizontalBox.reinterpret(),
            expand = 0,
            fill = 1,
            padding = 0,
            pack_type = GtkPackType.GTK_PACK_START
        )

        gtk_container_add(horizontalBox.reinterpret(), button.reinterpret())

        gtk_box_set_child_packing(
            box = horizontalBox,
            child = button.reinterpret(),
            expand = 1,
            fill = 1,
            padding = 0,
            pack_type = GtkPackType.GTK_PACK_START
        )

        gtk_widget_set_size_request(spinner.reinterpret(), SPINNER_WIDTH, 0)

        gtk_container_add(horizontalBox.reinterpret(), spinner.reinterpret())

        gtk_box_set_child_packing(
            box = horizontalBox,
            child = spinner.reinterpret(),
            expand = 0,
            fill = 1,
            padding = 0,
            pack_type = GtkPackType.GTK_PACK_START
        )

        gtk_container_add(verticalBox.reinterpret(), image.reinterpret())

        gtk_box_set_child_packing(
            box = verticalBox,
            child = image.reinterpret(),
            expand = 1,
            fill = 1,
            padding = 0,
            pack_type = GtkPackType.GTK_PACK_START
        )

        button.signalConnect0("clicked") {
            dispatch(Event.Reload)
        }
    }

    override fun show(model: ViewModel) {
        loadImage(model.kittenUrl)
        showLoading(model.isLoading)

        if (model.isError) {
            dispatch(Event.ErrorShown)
            showError()
        }
    }

    private fun loadImage(url: String?) {
        singleFromFunction { url?.let(::curl) }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(isThreadLocal = true, onSuccess = ::setImage)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            gtk_spinner_start(spinner)
        } else {
            gtk_spinner_stop(spinner)
        }
    }

    private fun showError() {
        val dialog: CPointer<GtkWidget> =
            requireNotNull(
                gtk_message_dialog_new(
                    parent = window,
                    flags = GTK_DIALOG_DESTROY_WITH_PARENT,
                    type = GtkMessageType.GTK_MESSAGE_ERROR,
                    buttons = GtkButtonsType.GTK_BUTTONS_CLOSE,
                    message_format = "Error loading kitten :-("
                )
            )

        dialog.signalConnect1("response") { _: gint ->
            gtk_widget_destroy(dialog.reinterpret())
        }

        gtk_widget_show(dialog)
    }

    private fun setImage(data: ByteArray?) {
        if ((data == null) || data.isEmpty()) {
            gtk_image_clear(image)

            return
        }

        memScoped {
            val arr: CArrayPointer<ByteVar> =
                allocArray(data.size) { index: Int ->
                    value = data[index]
                }

            val b: CPointer<GBytes> = g_bytes_new(arr, data.size.convert()).requireNotNull()
            val s: CPointer<GInputStream> = g_memory_input_stream_new_from_bytes(b).requireNotNull()

            val p: CPointer<GdkPixbuf> =
                gdk_pixbuf_new_from_stream_at_scale(
                    stream = s,
                    width = gtk_widget_get_allocated_width(image.reinterpret()),
                    height = gtk_widget_get_allocated_height(image.reinterpret()),
                    preserve_aspect_ratio = 1,
                    cancellable = null,
                    error = null
                )!!

            gtk_image_set_from_pixbuf(image, p)
        }
    }

    private companion object {
        private const val SPINNER_WIDTH = 32
        private const val WIDGET_MARGIN = 8
    }
}
