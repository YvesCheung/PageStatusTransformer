package com.huya.pitaya.ui.status

import android.view.View

/**
 * 状态切换时会控制[view]的可见行变化
 *
 * The visibility of [view] will be toggled during state switching.
 *
 * @author YvesCheung
 * 2020/4/17
 */
@Suppress("MemberVisibilityCanBePrivate")
open class SimpleStatus(protected val view: View?) : PageDisplayStatus {

    override fun showView() {
        view?.visible()
    }

    override fun hideView() {
        view?.gone()
    }
}