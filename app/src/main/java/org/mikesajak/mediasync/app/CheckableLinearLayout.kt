package org.mikesajak.mediasync.app

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.LinearLayout

/**
 * Created by mike on 17.01.16.
 */
class CheckableLinearLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), Checkable {
    private var checked = false

    companion object {
        private val CheckedStateSet: IntArray = intArrayOf(android.R.attr.state_checked)
    }

    constructor(context: Context) : this(context, null) {}

    override fun setChecked(b: Boolean) {
        checked = b
        refreshDrawableState()
    }
    override fun isChecked() = checked
    override fun toggle() { setChecked(!checked) }

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace)
        if (checked) {
            mergeDrawableStates(drawableState, CheckedStateSet)
        }
        return drawableState
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }
}