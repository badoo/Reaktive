package com.badoo.reaktive.samplemppmodule.binder

import com.badoo.reaktive.samplemppmodule.store.KittenStore.State
import com.badoo.reaktive.samplemppmodule.view.KittenView.ViewModel

internal object KittenStateToViewModelMapper {

    operator fun invoke(state: State): ViewModel =
        ViewModel(
            isLoading = state.isLoading,
            error = state.error,
            kittenUrl = state.kittenUrl
        )
}