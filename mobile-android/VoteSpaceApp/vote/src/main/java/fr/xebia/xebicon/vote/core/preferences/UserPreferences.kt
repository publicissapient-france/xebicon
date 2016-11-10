package fr.xebia.xebicon.vote.core.preferences

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(val context: Context) {

    companion object {
        val USER_PREFERENCES_NAME = "user_preferences"
        val USER_PREFERENCES_TRAIN_VOTED = "USER_PREFERENCES_TRAIN_VOTED"
    }

    fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun getVotedTrain(): Int {
        return getSharedPreferences().getInt(USER_PREFERENCES_TRAIN_VOTED, 0)
    }

    fun setVotedTrain(votedTrain: Int) {
        getSharedPreferences().edit().putInt(USER_PREFERENCES_TRAIN_VOTED, votedTrain).apply()
    }
}