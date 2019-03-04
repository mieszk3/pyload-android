package org.pyload.android.client.components

interface TabHandler {
    fun onSelected()

    fun onDeselected()

    fun setPosition(pos: Int)
}