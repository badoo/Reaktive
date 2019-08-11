package com.badoo.reaktive.samplemppmodule.binder

import com.badoo.reaktive.samplemppmodule.store.KittenStore.Intent
import com.badoo.reaktive.samplemppmodule.view.KittenView.Event

internal object KittenViewEventToIntentMapper {

    operator fun invoke(event: Event): Intent =
        when (event) {
            is Event.Reload -> Intent.Reload
        }
}