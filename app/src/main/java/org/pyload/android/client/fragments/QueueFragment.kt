package org.pyload.android.client.fragments

import android.content.Context

class QueueFragment : AbstractPackageFragment() {

    override fun onAttach(context: Context) {
        dest = 0
        super.onAttach(context)
    }
}