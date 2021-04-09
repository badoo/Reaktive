package com.badoo.reaktive.disposable

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

/**
 * Disposable whose underlying disposable can be replaced, causing this underlying resource's disposal.
 *
 * When [SerialDisposable] is disposed, it's underlying disposable is disposed and all disposables assigned
 * after that are disposed immediately.
 */
class SerialDisposable : Disposable {
    private val current = AtomicReference<Disposable?>(Disposable())

    /**
     * Contains the current disposable. If none has been set or [isDisposed], returns an empty disposable.
     * Disposes the current disposable upon setting.
     *
     * If [isDisposed], immediately disposes the new disposable.
     */
    var disposable: Disposable
        get() = current.value ?: Disposable()
        set(newDisposable) {
            current.update { currentDisposable ->
                val (toDispose, toReturn) = if (currentDisposable == null) {
                    Pair(newDisposable, null)
                } else {
                    Pair(currentDisposable, newDisposable)
                }

                toDispose.dispose()
                toReturn
            }
        }

    override val isDisposed: Boolean
        get() = current.value == null

    override fun dispose() {
        current.update { currentDisposable ->
            currentDisposable?.dispose()
            null
        }
    }
}
