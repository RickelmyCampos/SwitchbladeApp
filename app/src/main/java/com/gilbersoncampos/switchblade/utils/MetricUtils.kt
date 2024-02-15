package com.gilbersoncampos.switchblade.utils

import android.content.res.Resources

object MetricUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
    fun pxToDp(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}