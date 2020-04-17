package com.huya.pitaya.ui.status

import androidx.annotation.MainThread

/**
 * 状态ui最基本的表示，
 * 当切换到当前状态时，会触发[showView]，
 * 当切换别的状态时，会触发[hideView]。
 *
 * @author YvesCheung
 * 2020/4/17
 */
interface PageDisplayStatus {

    @MainThread
    fun showView()

    @MainThread
    fun hideView()
}