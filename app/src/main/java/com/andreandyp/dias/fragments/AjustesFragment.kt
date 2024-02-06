package com.andreandyp.dias.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.andreandyp.dias.R
import com.takisoft.preferencex.PreferenceFragmentCompat

class AjustesFragment : PreferenceFragmentCompat() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.show()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        val preferenceFile = this.getString(R.string.preference_file)
        preferenceManager.sharedPreferencesName = preferenceFile
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
