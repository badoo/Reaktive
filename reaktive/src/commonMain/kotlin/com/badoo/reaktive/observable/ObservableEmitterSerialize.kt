package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Emitter

fun <T> ObservableEmitter<T>.serialize(): ObservableEmitter<T> = SerializedObservableEmitter(this)

private class SerializedObservableEmitter<in T>(
    delegate: ObservableEmitter<T>
) : ObservableEmitter<T>, SerializedObservableCallbacks<T>(delegate), Emitter by delegate
