package com.isanechek.extractx.presentation.dashboard

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.isanechek.extractx.core.domain.models.QualityModel
import com.isanechek.extractx.core.domain.models.Resource
import com.isanechek.extractx.core.domain.models.YoutubeModel
import com.isanechek.extractx.core.extension.asLog
import com.isanechek.extractx.core.extension.hide
import com.isanechek.extractx.core.extension.onClick
import com.isanechek.extractx.core.platform.BaseScreen
import com.isanechek.extractx.presentation._layout
import com.isanechek.extractx.presentation._text
import com.sembozdemir.permissionskt.askPermissions
import com.sembozdemir.permissionskt.handlePermissionsResult
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.dashboard_screen_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardScreen : BaseScreen() {

    private val vm: DashboardViewModel by viewModel()
    private var defaultHint = "Enter url"
    private var videoName = "Video_${System.currentTimeMillis()}"
    override fun layoutId(): Int = _layout.dashboard_screen_layout

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObserver()
        dashboard_screen_add_content_btn.onClick {
            createEditDialog(defaultHint)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermissionsResult(requestCode, permissions, grantResults)
    }

    private fun createEditDialog(hintText: String) {
        MaterialDialog(requireActivity()).show {
            title(text = "Enter video url")
            cancelable(true)
            cancelOnTouchOutside(true)
            input(hint = hintText) { _, text ->
                val result = text.toString()
                vm.loadInfo(result)
                defaultHint = result
            }
            positiveButton(_text.dialog_enter_url_positive_btn_title) { dialog ->
                dialog.dismiss()
            }
            negativeButton(_text.dialog_enter_url_negative_btn_title) { dialog ->
                dialog.dismiss()
            }
        }
    }

    private fun setupObserver() {
        vm.response.observe(this, Observer { result ->
            result ?: return@Observer
            when (result) {
                is Resource.Loading -> {
                    "loading data".asLog(tag = TAG)
                    hideAddAndShowProgress()

                }
                is Resource.Success -> {
                    "loading done!".asLog(tag = TAG)
                    val model = result.data
                    if (model is YoutubeModel) {
                        model.title.asLog(tag = TAG)
                        hideProgressAndShowResult(model)
                    }
                }
                is Resource.Error -> {
                    "loading error!".asLog(tag = TAG, isError = true)
                    result.errorMessage.asLog(tag = TAG, isError = true)
                    hideProgressAndShowError(result.errorMessage)
                }
            }
        })

        vm.setupActionBtn.observe(this, Observer { info ->
            info ?: return@Observer
//            setupActionBtn(info)
        })

        vm.downloadState.observe(this, Observer { id ->
            id ?: return@Observer
            setupCancelBtn(id)
        })
    }

    private fun hideProgressAndShowError(errorMessage: String) {
        dashboard_screen_progress_container.hide(true)
    }

    private fun hideAddAndShowProgress() {
        dashboard_screen_add_content_container.hide(true)
        dashboard_screen_progress_container.visibility = View.VISIBLE

        dashboard_screen_cancel_request.onClick {
            hideProgressAndShowAdd()
            vm.cancelRequest()
        }
    }

    private fun hideProgressAndShowAdd() {
        dashboard_screen_progress_container.hide(true)
        dashboard_screen_add_content_container.visibility = View.VISIBLE
    }

    private fun hideProgressAndShowResult(model: YoutubeModel) {
        dashboard_screen_progress_container.hide(true)
        dashboard_screen_show_content_container.visibility = View.VISIBLE

        Picasso.get().load(model.maxThumbnail).transform(RoundedCornersTransformation(16, 0)).into(dashboard_screen_thumbnail_preview)

        videoName = model.title
        setupActionBtn(model, 0)
        dashboard_screen_show_content_action_btn.setOnLongClickListener {
            createQualityDialog(model)
            true
        }
    }

    private fun setupActionBtn(model: YoutubeModel, index: Int) {
        val m = model.qualities[index]
        with(dashboard_screen_show_content_action_btn) {
            text = m.height
            onClick {
                askPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    onGranted {
//                        vm.loadVideo(model.fileName, videoName)
                        vm.insertTask(model)
                    }
                    onShowRationale {
                        it.retry()
                    }
                    onDenied {  }
                    onNeverAskAgain {  }
                }
            }
        }
    }

    private fun setupCancelBtn(downloadId: Long) {
        with(dashboard_screen_show_content_action_btn) {
            text = "Cancel"
            onClick {
                vm.cancelDownload(downloadId)
            }
        }
    }

    private fun createQualityDialog(quality: YoutubeModel) {
        MaterialDialog(requireActivity()).show {
            title(text = "Choice quality")
            listItemsSingleChoice(
                items = quality.qualities.map { it.height }.toList(),
                initialSelection = 0
            ) { _, index, _ ->
                vm.setInfo(index)
            }
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    }

    companion object {
        private const val TAG = "DashboardScreen"
    }
}