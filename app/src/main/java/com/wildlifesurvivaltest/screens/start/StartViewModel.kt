package com.wildlifesurvivaltest.screens.start

import androidx.lifecycle.viewModelScope
import com.wildlifesurvivaltest.base.BaseViewModel
import com.wildlifesurvivaltest.data.prefs.PrefsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(preferences: PrefsEntity) :
    BaseViewModel<StartContract.Event, StartContract.State, StartContract.Effect>(preferences) {

    override fun createInitialState(): StartContract.State {
        return if (gameBegun) StartContract.State.ViewStateGameStarted
        else {
            points = 0
            StartContract.State.ViewStateGameNotStarted
        }
    }

    override fun handleEvent(event: StartContract.Event) {
        when (event) {
            StartContract.Event.OnNextClick -> {
                if (isConnected) {
                    gameBegun = true
                    setEffect { StartContract.Effect.NavigateToGameScreen }
                    viewModelScope.launch {
                        delay(500)
                        setState { StartContract.State.ViewStateGameStarted }
                    }
                } else {
                    setEffect { StartContract.Effect.ShowConnectionErrorDialog }
                }
            }
        }
    }

}