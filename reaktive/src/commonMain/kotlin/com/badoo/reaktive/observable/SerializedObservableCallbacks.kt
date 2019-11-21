package com.badoo.reaktive.observable

internal expect open class SerializedObservableCallbacks<in T>(delegate: ObservableCallbacks<T>) : ObservableCallbacks<T>
