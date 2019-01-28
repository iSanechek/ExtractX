package com.isanechek.extractx.presentation.download

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huxq17.download.DownloadInfo.Status.*
import com.isanechek.extractx.core.common.Utils.getDataSize
import com.isanechek.extractx.core.extension.inflate
import com.isanechek.extractx.presentation._id
import com.isanechek.extractx.presentation._layout
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class DownloadAdapter : RecyclerView.Adapter<DownloadAdapter.DownloadHolder>() {

    private val items = mutableListOf<DownloadItem>()
    private val map = mutableMapOf<DownloadHolder, DownloadItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadHolder = DownloadHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DownloadHolder, position: Int) {
        val item = items[position]
        holder.bindTo(item)
        item.info.tag = holder
        map[holder] = item
    }

    fun bindData(items: List<DownloadItem>, map: HashMap<DownloadHolder, DownloadItem>) {
        this.items.addAll(items)
        this.map.putAll(map)
        notifyDataSetChanged()
    }

    inner class DownloadHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(_layout.download_item_layout)) {

        private val cover = itemView.findViewById<ImageView>(_id.download_item_cover)
        private val progress = itemView.findViewById<ProgressBar>(_id.progressBar)
        private val title = itemView.findViewById<TextView>(_id.download_item_title_tv)
        private val size = itemView.findViewById<TextView>(_id.download_item_size_tv)
        private val status = itemView.findViewById<TextView>(_id.download_item_status_tv)
        private val speed = itemView.findViewById<TextView>(_id.download_item_speed_tv)

        fun bindTo(item: DownloadItem?) {
            item ?: return
            val info = item.info
            progress.progress = info.progress

            Picasso.get().load(item.coverUrl)
                .transform(RoundedCornersTransformation(16, 0))
                .into(cover)

            title.text = item.title

            val textSize = "${getDataSize(info.completedSize)}/${getDataSize(info.contentLength)}"
            size.text = textSize

            val s = item.info.status
            if (s != null) {
                when(s) {
                    STOPPED -> setStatus("Stopped")
                    WAIT -> setStatus("Wait")
                    PAUSED -> setStatus("Paused")
                    PAUSING -> setStatus("Pausing")
                    RUNNING -> {
                        setStatus("Running")
                        speed.text = item.info.speed
                    }
                    FINISHED -> setStatus("Finished")
                    FAILED -> setStatus("Retry")
                }
            }
        }

        private fun setStatus(statusMessage: String) {
            status.text = statusMessage
        }
    }
}