package com.wildlifesurvivaltest.screens.start

import com.wildlifesurvivaltest.base.UiEffect
import com.wildlifesurvivaltest.base.UiEvent
import com.wildlifesurvivaltest.base.UiState

class StartContract {

    sealed class Event : UiEvent {
        object OnNextClick : Event()
    }

    sealed class State : UiState {
        object ViewStateGameNotStarted: State()
        object ViewStateGameStarted: State()
    }

    sealed class Effect : UiEffect {
        object NavigateToGameScreen : Effect()
        object ShowConnectionErrorDialog : Effect()
    }
}