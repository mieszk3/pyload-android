package org.pyload.android.client

import android.os.Bundle
import android.view.MenuItem
import org.pyload.android.client.module.AppCompatPreferenceActivity
import org.pyload.android.client.utils.NavigationUtil

class Preferences : AppCompatPreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        supportActionBar?.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> NavigationUtil.navigateUp(this)
        }
        return true
    }
}