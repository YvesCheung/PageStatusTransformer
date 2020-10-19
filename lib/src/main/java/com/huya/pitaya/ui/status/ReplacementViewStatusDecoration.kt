package com.huya.pitaya.ui.status

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 装饰器模式，对[status]中的属性进行修改或增强。
 *
 * 比如有三种不同的ui特性：
 * ```kotlin
 * class Feature1Decorate(status: ReplacementViewStatus)：ReplacementViewStatusDecoration
 * class Feature2Decorate(status: ReplacementViewStatus)：ReplacementViewStatusDecoration
 * class Feature3Decorate(status: ReplacementViewStatus)：ReplacementViewStatusDecoration
 * ```
 * 通过装饰器嵌套的方式来组合这些ui的特性：
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

    override var parent: ViewGroup?
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

    override fun onViewShow(view: View, param: Map<String, Any>) = status.onViewShow(view, param)

    override fun onViewShow(view: View) = status.onViewShow(view)

    override fun onViewHide(view: View) = status.onViewHide(view)
}