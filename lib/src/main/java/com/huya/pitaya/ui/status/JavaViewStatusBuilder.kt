package com.huya.pitaya.ui.status

/**
 * 没有什么实际意义，主要是为了兼容Java的语言和Kotlin的DSL
 * 使得[PageStatusTransformer.ViewStatusBuilder]的方法调用在Java里面不会太恶心
 *
 * @author YvesCheung
 * 2020/3/26
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class JavaViewStatusBuilder {

    private val map = mutableMapOf<String, PageDisplayStatus>()

    fun putStatus(status: Enum<*>, view: PageDisplayStatus): JavaViewStatusBuilder =
        putStatus(status.name, view)

    fun putStatus(statusAndView: Pair<String, PageDisplayStatus>): JavaViewStatusBuilder =
        putStatus(statusAndView.first, statusAndView.second)

    fun putStatus(status: String, view: PageDisplayStatus): JavaViewStatusBuilder {
        map[status] = view
        return this
    }

    fun build(): Function1<PageStatusTransformer.ViewStatusBuilder, Unit> {
        return { builder ->
            with(builder) {
                map.entries.forEach { (status, view) ->
                    status {
                        view
                    }
                }
            }
        }
    }
}