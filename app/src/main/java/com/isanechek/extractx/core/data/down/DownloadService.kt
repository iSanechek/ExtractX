package com.isanechek.extractx.core.data.down

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.io.File

class DownloadService(
    private val context: Context
) : DownloadServiceContract {

    private fun isExternalStorageWritable(): Boolean =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    private fun isExternalStorageReadable(): Boolean =
        Environment.getExternalStorageState() in setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)

    private fun hasFilePermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    override fun download(requestUrl: String, title: String, fileName: String, callback: (DownloadResponse) -> Unit) {

        if (!hasFilePermission(context)) {
            callback.invoke(DownloadResponse.Error("Need request permission!"))
            return
        }

        if (!isExternalStorageWritable()) {
            callback.invoke(DownloadResponse.Error("External Storage Readonly!"))
            return
        }

        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "ExtractX")
        if (!directory.exists() || directory.isDirectory) {
            directory.mkdirs()
            val nomedia = File(directory, ".nomedia")
            if (!nomedia.exists()) {
                nomedia.createNewFile()
            }
        }



        val request = DownloadManager.Request(requestUrl.toUri())
        request.setTitle(title)
        request.setMimeType(".mp4")
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverRoaming(true)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, directory.absolutePath)
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val requestId = manager.enqueue(request)
        callback.invoke(DownloadResponse.Load(requestId))

    }

    override fun removeDownloads(vararg ids: Long) {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        for (id in ids) {
            manager.remove(id)
            val uri = manager.getUriForDownloadedFile(id)
            if (uri != null) {
                val file = File(uri.toString())
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }
}