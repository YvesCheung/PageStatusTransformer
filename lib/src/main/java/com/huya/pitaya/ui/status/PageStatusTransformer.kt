package com.huya.pitaya.ui.status

import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children

/**
 * # 页面状态切换
 * - 定义状态，返回状态要显示的视图
 * ```Kotlin
 * val status = PageStatusTransformer.newInstance {
 *    "状态1" {
 *      ViewStatus(view_for_status_1)
 *    }
 *    "状态2" {
 *      ViewStatus(view_for_status_2)
 *    }
 *    "状态3" {
 *      ViewStatus(view_for_status_3)
 *    }
 * }
 * ```
 *
 * - 切换状态
 * ```Kotlin
 * status.transform("状态3")
 * ```
 *
 * # 页面的状态
 * - 每一种状态对应一个[PageDisplayStatus]，切换状态意味着：
 *     - 当前状态调用 [PageDisplayStatus.showView]
 *     - 其他非当前状态调用 [PageDisplayStatus.hideView]
 *
 * - 状态可以通过[String]或者[Enum]来命名
 *     - 通过[newInstance]静态工厂构造时需要为所有可能使用的状态进行命名和定义。
 *     通过[ViewStatusBuilder.invoke]方法会把这些状态记录到状态机中。
 *
 * # 状态的可见性
 * - 通常只有命名为[currentStatusName]的当前状态是可见的，
 * 其余所有状态都会因为[PageDisplayStatus.hideView]被调用而不可见；
 * - 也可以通过设置[visibility]为 `false` 让所有状态（包括[currentStatusName]）变得不可见，
 * 设置为`true`可以让当前状态恢复可见。
 *
 * @see SimpleStatus
 * @see ViewStubStatus
 * @see ReplacementViewStatus
 *
 * @author YvesCheung
 * 2020/3/21
 */
@Suppress("MemberVisibilityCanBePrivate")
class PageStatusTransformer private constructor() {

    companion object {

        //static factory
        @JvmStatic
        fun newInstance(config: ViewStatusBuilder.() -> Unit): PageStatusTransformer {
            val instance = PageStatusTransformer()
            config(instance.ViewStatusBuilder())
            return instance
        }

        //static factory
        @JvmStatic
        fun newInstance(
            replaceTo: View,
            config: ReplacementViewStatusBuilder.() -> Unit
        ): PageStatusTransformer {
            val instance = PageStatusTransformer()
            config(instance.ReplacementViewStatusBuilder(replaceTo))
            return instance
        }
    }

    private val statusList = mutableMapOf<String, PageDisplayStatus>()

    /**
     * 状态的构造，可以通过:
     * ```kotlin
     * "yourStatusName" {
     *      PageDisplayStatus()
     * }
     * ```
     * 或者
     * ```
     * YourStatusEnum {
     *      PageDisplayStatus()
     * }
     * 来定义状态机中包含的所有状态
     * ```
     *
     * 在Java中使用[JavaViewStatusBuilder]来适配这个DSL
     */
    open inner class ViewStatusBuilder internal constructor() {

        //DSL constructor
        open operator fun String.invoke(createStatus: () -> PageDisplayStatus) {
            statusList[this] = createStatus().also { status ->
                if (status is ReplacementViewStatus) {
                    throw IllegalArgumentException(
                        "Use `PageStatusTransformer.newInstance" +
                                "(replaceTo = placeHolderView) {...}` instead."
                    )
                }
            }
        }

        //DSL constructor
        open operator fun Enum<*>.invoke(createStatus: () -> PageDisplayStatus) {
            this.name.invoke(createStatus)
        }
    }

    /**
     * 状态的构造，可以通过:
     * ```kotlin
     * "yourStatusName" {
     *      PageDisplayStatus()
     * }
     * ```
     * 或者
     * ```
     * YourStatusEnum {
     *      PageDisplayStatus()
     * }
     * 来定义状态机中包含的所有状态
     * ```
     *
     * 在Java中使用[JavaViewStatusBuilder]来适配这个DSL
     */
    open inner class ReplacementViewStatusBuilder internal constructor(
        /**
         * `replaceTo` 参数指定的 `View`
         */
        val contentView: View
    ) : ViewStatusBuilder() {

        private val tagKey get() = R.id.tag_page_status_transformer

        override fun String.invoke(createStatus: () -> PageDisplayStatus) {
            statusList[this] = createStatus().also { status ->
                if (status is ReplacementViewStatus) {
                    status.parent = replaceParent(contentView)
                }
            }
        }

        private fun replaceParent(contentView: View): ViewGroup {
            if (contentView is ViewGroup && contentView.getTag(tagKey) != null) {
                return contentView
            }
            when (val grandParent = contentView.parent) {
                is FrameLayout -> {
                    grandParent.setTag(tagKey, "Make from PageStatusTransformer")
                    return grandParent
                }
                is ViewGroup -> {
                    if (grandParent.getTag(tagKey) != null) {
                        return grandParent
                    }
                    val index = grandParent.indexOfChild(contentView)
                    val contentParam = contentView.layoutParams
                    grandParent.removeView(contentView)
                    val newParent = FrameLayout(grandParent.context)
                    newParent.id = ViewCompat.generateViewId()
                    grandParent.addView(newParent, index, contentParam)
                    newParent.addView(contentView, MATCH_PARENT, MATCH_PARENT)
                    newParent.setTag(tagKey, "Make from PageStatusTransformer")
                    resolveConstraintLayoutId(grandParent, contentView, newParent)
                    return newParent
                }
                else -> {
                    throw IllegalStateException(
                        "$contentView must have a parent. " +
                                "Current parent is $grandParent"
                    )
                }
            }
        }

        /**
         * ConstraintLayout会依赖原来的View的id（比如layout_constraintBottom_toBottomOf="旧id"），
         * 需要把旧id全部替换为新id。
         */
        private fun resolveConstraintLayoutId(constraintLayout: ViewGroup, old: View, new: View) {
            val oldId = old.id
            val newId = new.id
            if (oldId != View.NO_ID && constraintLayout is ConstraintLayout) {
                constraintLayout.children.forEach { child ->
                    val lp = child.layoutParams
                    if (lp is ConstraintLayout.LayoutParams) {
                        if (lp.baselineToBaseline == oldId) lp.baselineToBaseline = newId
                        if (lp.leftToLeft == oldId) lp.leftToLeft = newId
                        if (lp.leftToRight == oldId) lp.leftToRight = newId
                        if (lp.topToTop == oldId) lp.topToTop = newId
                        if (lp.topToBottom == oldId) lp.topToBottom = newId
                        if (lp.rightToRight == oldId) lp.rightToRight = newId
                        if (lp.rightToLeft == oldId) lp.rightToLeft = newId
                        if (lp.bottomToBottom == oldId) lp.bottomToBottom = newId
                        if (lp.bottomToTop == oldId) lp.bottomToTop = newId
                        if (lp.circleConstraint == oldId) lp.circleConstraint = newId
                    }
                }
            }
        }
    }

    /**
     * 当前显示的状态，通过[transform]切换
     */
    @Volatile
    var currentStatusName: String? = null
        private set

    /**
     * 控制当前状态的可见性。
     * 该方法不影响通过[transform]切换状态，即使[visibility]为false，依然可以切换状态，但是状态不可见。
     * 当[visibility]切换为true时，[currentStatusName]代表的状态就会显示。
     */
    var visibility: Boolean = true
        @MainThread
        set(visible) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw IllegalThreadStateException("Only the mainThread can change ui visibility")
            }
            if (field != visible) {
                field = visible
                if (visible) {
                    currentStatusName?.let { status -> transform(status) }
                } else {
                    statusList.values.forEach { status -> status.hideView() }
                }
            }
        }

    /**
     * 切换状态
     * @param status 在[ViewStatusBuilder]中定义的状态
     */
    @MainThread
    fun transform(status: Enum<*>) = transform(status.name)

    /**
     * 切换状态
     * @param status 在[ViewStatusBuilder]中定义的状态
     */
    @MainThread
    fun transform(status: String) {
        currentStatusName = status
        if (visibility) {
            val currentStatus = statusList[status]
            when {
                currentStatus == null -> {
                    throw IllegalArgumentException(
                        "PageStatusTransformer has status: '${statusList.keys}'," +
                                " but expect status is '$status'"
                    )
                }
                Looper.myLooper() != Looper.getMainLooper() -> {
                    throw IllegalThreadStateException("Only the mainThread can transform the ui status")
                }
                else -> {
                    statusList.filter { it.key != status }.forEach { (_, status) ->
                        status.hideView()
                    }
                    currentStatus.showView()
                }
            }
        }
    }
}