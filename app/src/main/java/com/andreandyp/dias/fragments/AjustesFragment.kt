package com.andreandyp.dias.fragments

import android.content.Context
import android.os.Bundle
import com.andreandyp.dias.R
import com.takisoft.preferencex.PreferenceFragmentCompat

class AjustesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        val preferenceFile = this.getString(R.string.preference_file)
        preferenceManager.sharedPreferencesName = preferenceFile
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}