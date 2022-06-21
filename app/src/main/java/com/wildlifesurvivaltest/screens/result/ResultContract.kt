package com.wildlifesurvivaltest.screens.result

import android.app.Activity
import android.content.Context
import com.wildlifesurvivaltest.base.UiEffect
import com.wildlifesurvivaltest.base.UiEvent
import com.wildlifesurvivaltest.base.UiState

class ResultContract {

    sealed class Event : UiEvent {
        object OnShareResultsClick : Event()
        object OnRestartGameClick : Event()
        object OnRateUsClick : Event()
        object OnAnotherAppsClick : Event()
        data class OnPayClick(val activity: Activity) : Event()
    }

    sealed class State : UiState {
        object ViewStateGameResult : State()
    }

    sealed class Effect : UiEffect {
        object OpenShareActivity : Effect()
        object OpenRateUsActivity : Effect()
        object OpenAnotherAppsActivity : Effect()
        object NavigateToStartScreen : Effect()
        object ShowConnectionErrorDialog : Effect()
    }

}