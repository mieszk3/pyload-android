package org.pyload.android.client.components

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ScrollingTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyle) {
    override fun onFocusChanged(focused: Boolean, direction: Int,
                                previouslyFocusedRect: Rect?) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect)
        }
    }

    override fun onWindowFocusChanged(focused: Boolean) {
        if (focused) {
            super.onWindowFocusChanged(focused)
        }
    }

    override fun isFocused(): Boolean {
        return true
    }
}