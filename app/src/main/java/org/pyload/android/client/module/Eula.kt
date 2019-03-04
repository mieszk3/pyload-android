/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pyload.android.client.module

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences

import org.pyload.android.client.R

import java.io.BufferedReader
import java.io.Closeable
import java.io.IOException
import java.io.InputStreamReader

/**
 * Displays an EULA ("End User License Agreement") that the user has to accept before
 * using the application. Your application should call [Eula.show]
 * in the onCreate() method of the first activity. If the user accepts the EULA, it will never
 * be shown again. If the user refuses, [android.app.Activity.finish] is invoked
 * on your activity.
 */
object Eula {
    private const val ASSET_EULA = "EULA"
    private const val PREFERENCE_EULA_ACCEPTED = "eula.accepted"
    private const val PREFERENCES_EULA = "eula"

    /**
     * callback to let the activity know when the user has accepted the EULA.
     */
    internal interface OnEulaAgreedTo {

        /**
         * Called when the user has accepted the eula and the dialog closes.
         */
        fun onEulaAgreedTo()
    }

    /**
     * Displays the EULA if necessary. This method should be called from the onCreate()
     * method of your main Activity.
     *
     * @param activity The Activity to finish if the user rejects the EULA.
     * @return Whether the user has agreed already.
     */
    @JvmStatic
    fun show(activity: Activity): Boolean {
        val preferences = activity.getSharedPreferences(PREFERENCES_EULA,
                Activity.MODE_PRIVATE)
        if (!preferences.getBoolean(PREFERENCE_EULA_ACCEPTED, false)) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(R.string.eula_title)
            builder.setCancelable(true)
            builder.setPositiveButton(R.string.eula_accept) { _, _ ->
                accept(preferences)
                if (activity is OnEulaAgreedTo) {
                    activity.onEulaAgreedTo()
                }
            }
            builder.setNegativeButton(R.string.eula_refuse) { _, _ -> refuse(activity) }
            builder.setOnCancelListener { refuse(activity) }
            builder.setMessage(readEula(activity))
            builder.create().show()
            return false
        }
        return true
    }

    private fun accept(preferences: SharedPreferences) {
        preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, true).apply()
    }

    private fun refuse(activity: Activity) {
        activity.finish()
    }

    private fun readEula(activity: Activity): CharSequence {
        var bufferedReader: BufferedReader? = null
        return try {
            bufferedReader = BufferedReader(InputStreamReader(activity.assets.open(ASSET_EULA)))
            var line: String? = bufferedReader.readLine()
            val buffer = StringBuilder()
            while (line != null) {
                buffer.append(line).append('\n')
                line = bufferedReader.readLine()
            }
            buffer
        } catch (e: IOException) {
            ""
        } finally {
            closeStream(bufferedReader)
        }
    }

    /**
     * Closes the specified stream.
     *
     * @param stream The stream to close.
     */
    private fun closeStream(stream: Closeable?) {
        try {
            stream?.close()
        } catch (e: IOException) {
            // Ignore
        }
    }
}
