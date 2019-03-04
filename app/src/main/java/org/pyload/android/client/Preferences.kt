package org.pyload.android.client

import android.os.Bundle
import android.view.MenuItem
import org.pyload.android.client.module.AppCompatPreferenceActivity

class Preferences : AppCompatPreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}