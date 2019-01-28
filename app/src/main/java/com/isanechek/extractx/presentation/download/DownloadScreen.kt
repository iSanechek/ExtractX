package com.isanechek.extractx.presentation.download

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.huxq17.download.Pump
import com.huxq17.download.listener.DownloadObserver
import com.isanechek.extractx.core.platform.BaseScreen
import com.isanechek.extractx.presentation._layout
import kotlinx.android.synthetic.main.download_screen_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DownloadScreen : BaseScreen() {

    private var map: HashMap<DownloadAdapter.DownloadHolder, DownloadItem> = HashMap()
    private val vm: DownloadViewModel by viewModel()
    private val downloadObserver = object : DownloadObserver() {
        override fun onProgress(progress: Int) {
            val item = downloadInfo
            val h = item.tag
            if (h != null) {
                val holder = h as DownloadAdapter.DownloadHolder
                val tag = map[holder]
                if (tag != null && tag.info.filePath == item.filePath) {
                    holder.bindTo(vm.mapInfo(tag.info))
                }
            }
        }
    }

    override fun layoutId(): Int = _layout.download_screen_layout

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val _adapter = DownloadAdapter()
        with(download_screen_rv) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = _adapter
        }
        _adapter.bindData(vm.mapAllTask(), map)
        Pump.subscribe(downloadObserver)
    }

    override fun onDetach() {
        super.onDetach()
        Pump.unSubscribe(downloadObserver)
    }
}