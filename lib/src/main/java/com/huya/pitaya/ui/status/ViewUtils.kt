package com.huya.pitaya.ui.status

import android.view.View

/**
 * @author YvesCheung
 * 2020/4/17
 */
@DslMarker
private annotation class ViewUtils

@ViewUtils
internal fun <T : View> T.visible(): T = apply { visibility = View.VISIBLE }

@ViewUtils
internal fun <T : View> T.gone(): T = apply { visibility = View.GONE }