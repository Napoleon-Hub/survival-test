package com.wildlifesurvivaltest.screens.game

import android.content.Context
import com.wildlifesurvivaltest.base.UiEffect
import com.wildlifesurvivaltest.base.UiEvent
import com.wildlifesurvivaltest.base.UiState
import com.wildlifesurvivaltest.domain.models.Answer

class GameContract {

    sealed class Event : UiEvent {
        data class OnAnswerClick(val answer: Answer) : Event()
        data class OnNextClick(val isFinish: Boolean, val context: Context) : Event()
    }

    sealed class State : UiState {
        object ViewStatePlayGame: State()
        object ViewStateFinishGame: State()
    }

    sealed class Effect : UiEffect {
        object NavigateToResultScreen : Effect()
        object ShowConnectionErrorDialog : Effect()
        object ShowChooseAnswerToast : Effect()
    }

}