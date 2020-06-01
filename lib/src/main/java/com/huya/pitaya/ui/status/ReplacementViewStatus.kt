package com.huya.pitaya.ui.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 状态切换时会顶替原来位置的 [View]，大小/父布局与被顶替的 [View] 相同。
 * 被顶替的 [View] 由 [PageStatusTransformer.newInstance] 的 `replaceTo` 参数设置。
 *
 * 可重用的ui的修改，通过[ReplacementViewStatusDecoration]装饰器嵌套来实现，而不是继承。
 *
 * @author YvesCheung
 * 2020/4/17
 */
abstract class ReplacementViewStatus : PageDisplayStatus {

    open var parent: ViewGroup? = null

    open var child: View? = null

    abstract fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View

    private fun getOrCreateChild(): View {
        return child
            ?: inflateView(LayoutInflater.from(parent!!.context), parent!!).also { child = it }
    }

    final override fun showView() {
        val view = getOrCreateChild()
        val originParent = view.parent
        if (originParent == parent) {
            return
        } else if (originParent != null) {
            if (originParent is ViewGroup) {
                originParent.removeView(view)
            } else {
                throw IllegalStateException("Why $view has a parent $originParent?")
            }
        }
        parent!!.addView(view)
        onViewShow(view)
    }

    /**
     * 子类覆写[onViewShow]而不是[showView]，为的是[ReplacementViewStatusDecoration]能正常嵌套代理
     */
    open fun onViewShow(view: View) {}

    final override fun hideView() {
        val view = child
        if (view != null) {
            parent!!.removeView(view)
            onViewHide(view)
        }
    }

    /**
     * 子类覆写[onViewHide]而不是[hideView]，为的是[ReplacementViewStatusDecoration]能正常嵌套代理
     */
    open fun onViewHide(view: View) {}
}