package org.pyload.android.client.fragments

import android.content.Context

class CollectorFragment : AbstractPackageFragment() {

    override fun onAttach(context: Context?) {
        dest = 1
        super.onAttach(context)
    }
}