package org.pyload.android.client.utils

import android.app.Activity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder

object NavigationUtil {
    fun navigateUp(activity: Activity): Boolean {
        val upIntent = NavUtils.getParentActivityIntent(activity)
        return if (upIntent != null) {
            if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
                // This activity is NOT part of this app's task, so
                // create a new task when navigating up, with a
                // synthesized back stack.
                TaskStackBuilder.create(activity)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(activity, upIntent);
            }
            true
        } else {
            false
        }
    }
}