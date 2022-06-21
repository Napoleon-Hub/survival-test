package com.wildlifesurvivaltest.data.prefs

import android.content.Context
import com.wildlifesurvivaltest.data.prefs.types.activeBoolean
import com.wildlifesurvivaltest.data.prefs.types.activeInt
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefsEntity @Inject constructor(@ApplicationContext val ctx: Context) {
    var isConnected by activeBoolean(false)
    var gameBegun by activeBoolean(false)
    var lastQuestionIndex by activeInt(0)
    var points by activeInt(0)
}
