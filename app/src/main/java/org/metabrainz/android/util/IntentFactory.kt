package org.metabrainz.android.util

import android.content.Context
import android.content.Intent
import org.metabrainz.android.ui.screens.settings.SettingsActivity

object IntentFactory {
    fun getSettings(context: Context?): Intent {
        return Intent(context, SettingsActivity::class.java)
    }
}