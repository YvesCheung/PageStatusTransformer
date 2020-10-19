package com.huya.pitaya.pagestatustransformer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huya.pitaya.ui.status.PageStatusTransformer
import com.huya.pitaya.ui.status.ReplacementViewStatus
import com.huya.pitaya.ui.status.ReplacementViewStatusDecoration
import com.huya.pitaya.ui.status.SimpleStatus
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.error_status.view.*
import kotlinx.android.synthetic.main.loading_status.view.*
import kotlinx.android.synthetic.main.empty_status.view.*

class MainActivity : AppCompatActivity() {

    private val statusTransformer = mutableListOf<PageStatusTransformer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapOf(
            "LinearLayout" to linear_layout_content,
            "FrameLayout" to frame_layout_content,
            "RelativeLayout" to relative_layout_content,
            "ConstraintLayout" to constraint_layout_content
        ).entries.forEach { (name, view) ->

            val transformer =
                PageStatusTransformer.newInstance(replaceTo = view) {

                    DemoStatus.Normal {
                        SimpleStatus(contentView)
                    }

                    DemoStatus.Loading {
                        MyLoadingPageStatus("$name Loading...")
                    }

                    DemoStatus.NothingHappen {
                        MyEmptyPageStatus("$name Empty？")
                    }

                    DemoStatus.EncounterAnError {
                        MyErrorPageStatus("$name Error!")
                    }

                    DemoStatus.EncounterVeryBigError {
                        BigLogoDecoration(MyErrorPageStatus("$name Big Error!!!"))
                    }
                }
            statusTransformer.add(transformer)
        }
    }

    private class MyLoadingPageStatus(private val text: String) : ReplacementViewStatus() {

        override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
            val root = inflater.inflate(R.layout.loading_status, parent, false)
            root.loading_text.text = text
            return root
        }
    }

    private class MyEmptyPageStatus(private val text: String) : ReplacementViewStatus() {

        override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
            val root = inflater.inflate(R.layout.empty_status, parent, false)
            root.empty_text.text = text
            return root
        }
    }

    private class MyErrorPageStatus(private val text: String) : ReplacementViewStatus() {

        override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
            val root = inflater.inflate(R.layout.error_status, parent, false)
            root.error_text.text = text
            return root
        }
    }

    private class BigLogoDecoration(anyOtherStatus: ReplacementViewStatus) :
        ReplacementViewStatusDecoration(anyOtherStatus) {

        override fun onViewShow(view: View) {
            super.onViewShow(view)
            view.error_img?.layoutParams?.let {
                it.width = 500
                it.height = 500
                view.error_img?.layoutParams = it
            }
        }
    }

    enum class DemoStatus {
        Normal,

        Loading,

        NothingHappen,

        EncounterAnError,

        EncounterVeryBigError
    }

    var currentState = 0

    fun onClickChangeStatus(v: View) {
        val allStatus = DemoStatus.values()
        val nextStatus = allStatus[++currentState % allStatus.size]
        statusTransformer.forEach { pageStatusTransformer ->
            pageStatusTransformer.transform(nextStatus)
        }
    }
}
