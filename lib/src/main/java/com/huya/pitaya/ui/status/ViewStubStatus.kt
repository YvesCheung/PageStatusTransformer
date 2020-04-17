package com.huya.pitaya.ui.status

import android.view.View
import android.view.ViewStub

/**
 * 状态切换时会触发 [ViewStub] 的渲染。
 *
 * @author YvesCheung
 * 2020/4/17
 */
@Suppress("MemberVisibilityCanBePrivate")
open class ViewStubStatus(protected val viewStub: ViewStub) : PageDisplayStatus {

    protected var actualView: View? = null

    override fun showView() {
        val view = actualView
            ?: viewStub.inflate().also { actualView = it }
        view.visible()
    }

    override fun hideView() {
        actualView?.gone()
    }
}