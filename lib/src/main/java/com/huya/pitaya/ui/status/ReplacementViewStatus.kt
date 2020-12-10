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
 * When the state is switched, the [View] in the original hierarchy will be replaced by a new [inflateView],
 * which has the same size and same parent.
 *
 * The substituted [View] is replaced by the `replaceTo` parameter set in [PageStatusTransformer.newInstance].
 *
 * The modification of reusable UI is implemented by nesting the [ReplacementViewStatusDecoration] decorator
 * rather than by inheritance.
 *
 * @author YvesCheung
 * 2020/4/17
 */
abstract class ReplacementViewStatus : PageDisplayStatus {

    open var parent: ParentInfo? = null
        internal set

    open var child: View? = null

    abstract fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View

    private fun getOrCreateChild(): View {
        val parentView = parent!!.view
        return child
            ?: inflateView(LayoutInflater.from(parentView.context), parentView).also { child = it }
    }

    final override fun showView() {
        //No Access
    }

    final override fun showView(param: Map<String, Any>) {
        val p = parent!!
        val view = getOrCreateChild()
        val originParent = view.parent
        if (originParent == p.view) {
            return
        } else if (originParent != null) {
            if (originParent is ViewGroup) {
                originParent.removeView(view)
            } else {
                throw IllegalStateException("Why $view has a parent $originParent?")
            }
        }
        val index = if (p.insertIndex <= p.view.childCount) p.insertIndex else -1
        p.view.addView(view, index)

        onViewShow(view, param)
    }

    /**
     * 子类覆写[onViewShow]而不是[showView]，为的是[ReplacementViewStatusDecoration]能正常嵌套代理
     *
     * Subclasses must override [onViewShow] instead of [showView]
     * so that [ReplacementViewStatusDecoration] can work properly.
     */
    open fun onViewShow(view: View, param: Map<String, Any>) = onViewShow(view)

    open fun onViewShow(view: View) {}

    final override fun hideView() {
        val p = parent!!
        val view = child
        if (view != null) {
            p.view.removeView(view)
            onViewHide(view)
        }
    }

    /**
     * 子类覆写[onViewHide]而不是[hideView]，为的是[ReplacementViewStatusDecoration]能正常嵌套代理
     *
     * Subclasses must override [onViewHide] instead of [hideView]
     * so that [ReplacementViewStatusDecoration] can work properly.
     */
    open fun onViewHide(view: View) {}
}