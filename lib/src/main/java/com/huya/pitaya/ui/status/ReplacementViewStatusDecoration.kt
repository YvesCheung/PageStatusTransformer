package com.huya.pitaya.ui.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 装饰器模式，对[status]中的属性进行修改或增强。
 *
 * Decorator pattern to modify or enhance ability in [status].
 *
 * If we have 3 different features:
 *
 * ```kotlin
 * class Feature1Decorate(status: ReplacementViewStatus)：ReplacementViewStatusDecoration
 * class Feature2Decorate(status: ReplacementViewStatus)：ReplacementViewStatusDecoration
 * class Feature3Decorate(status: ReplacementViewStatus)：ReplacementViewStatusDecoration
 * ```
 *
 * We can combine these features by nesting decoration:
 *
 * ```kotlin
 * val viewStatus: PageDisplayStatus =
 *      Feature1Decorate(
 *          Feature2Decorate(
 *              Feature3Decorate(actualStatus)
 *          )
 *      )
 * ```
 *
 * @author YvesCheung
 * 2020/4/17
 */
abstract class ReplacementViewStatusDecoration(
    protected val status: ReplacementViewStatus
) : ReplacementViewStatus() {

    override var parent: ParentInfo?
        get() = status.parent
        set(value) {
            status.parent = value
        }

    override var child: View?
        get() = status.child
        set(value) {
            status.child = value
        }

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View =
        status.inflateView(inflater, parent)

    override fun onViewShow(view: View, param: Map<String, Any>) {
        status.onViewShow(view, param)
        onViewShow(view)
    }

    override fun onViewHide(view: View) = status.onViewHide(view)
}