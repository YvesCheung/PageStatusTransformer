package com.huya.pitaya.ui.status

import androidx.annotation.MainThread

/**
 * 状态ui最基本的表示，
 * 当切换到当前状态时，会触发[showView]，
 * 当切换别的状态时，会触发[hideView]。
 *
 * The most basic representation of the state UI.
 *
 * When switching to the current state, [showView] will be invoked.
 *
 * When switching other states, the [hideView] will be invoked.
 *
 * @see SimpleStatus
 * @see ViewStubStatus
 * @see ReplacementViewStatus
 *
 * @author YvesCheung
 * 2020/4/17
 */
interface PageDisplayStatus {

    /**
     * @param param passed by [PageStatusTransformer.transform]
     */
    @MainThread
    fun showView(param: Map<String, Any>) = showView()

    @MainThread
    fun showView() {}

    @MainThread
    fun hideView() {}
}