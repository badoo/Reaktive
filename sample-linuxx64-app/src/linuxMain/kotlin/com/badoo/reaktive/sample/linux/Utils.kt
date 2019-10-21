package com.badoo.reaktive.sample.linux

import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.staticCFunction
import libgtk3.GClosure
import libgtk3.g_signal_connect_data
import libgtk3.gint

fun <T : Any> T?.requireNotNull(): T = requireNotNull(this)

fun CPointer<*>.signalConnect0(signal: String, handler: () -> Unit) {
    g_signal_connect_data(
        instance = this,
        detailed_signal = signal,
        c_handler = staticCFunction(::onSignal0).reinterpret(),
        data = StableRef.create(handler).asCPointer(),
        destroy_data = staticCFunction(::destroyStableRef),
        connect_flags = 0U
    )
}

fun <T : CPointed> CPointer<*>.signalConnect1(signal: String, handler: (T) -> Unit) {
    g_signal_connect_data(
        instance = this,
        detailed_signal = signal,
        c_handler = staticCFunction(::onSignal1).reinterpret(),
        data = StableRef.create(handler).asCPointer(),
        destroy_data = staticCFunction(::destroyStableRef),
        connect_flags = 0U
    )
}

fun CPointer<*>.signalConnect1(signal: String, handler: (gint) -> Unit) {
    g_signal_connect_data(
        instance = this,
        detailed_signal = signal,
        c_handler = staticCFunction(::onSignalInt).reinterpret(),
        data = StableRef.create(handler).asCPointer(),
        destroy_data = staticCFunction(::destroyStableRef),
        connect_flags = 0U
    )
}

private fun onSignal0(@Suppress("UNUSED_PARAMETER") instance: CPointer<*>, data: CPointer<*>) {
    data.asStableRef<() -> Unit>().get().invoke()
}

private fun onSignal1(@Suppress("UNUSED_PARAMETER") instance: CPointer<*>, param: CPointer<CPointed>, data: CPointer<*>) {
    data.asStableRef<(CPointed) -> Unit>().get().invoke(param.pointed)
}

private fun onSignalInt(@Suppress("UNUSED_PARAMETER") instance: CPointer<*>, param: gint, data: CPointer<*>) {
    data.asStableRef<(gint) -> Unit>().get().invoke(param)
}

private fun destroyStableRef(data: CPointer<*>?, @Suppress("UNUSED_PARAMETER") closure: CPointer<GClosure>?) {
    data?.asStableRef<Any>()?.dispose()
}
