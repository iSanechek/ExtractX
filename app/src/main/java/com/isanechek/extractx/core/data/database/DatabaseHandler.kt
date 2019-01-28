package com.isanechek.extractx.core.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import com.isanechek.extractx.core.domain.models.DownloadTask

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private fun addTask(task: DownloadTask, db: SQLiteDatabase): Boolean {
        val cv = ContentValues()
        with(cv) {
            put(DownloadTask.ID, task.id)
            put(DownloadTask.TITLE, task.title)
            put(DownloadTask.COVER_URL, task.cover)
        }
        val _success = db.insert(DownloadTask.DB_TABLE_NAME, null, cv)
        return _success.toInt() != -1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableDownloadTasks = "CREATE TABLE ${DownloadTask.DB_TABLE_NAME} (" +
                "${DownloadTask.ID} $DB_COLUMN_TEXT, " +
                "${DownloadTask.TITLE} $DB_COLUMN_TEXT, " +
                "${DownloadTask.COVER_URL} $DB_COLUMN_TEXT);"
        db?.execSQL(createTableDownloadTasks)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropDownloadTaskTAble = "DROP TABLE IF EXISTS ${DownloadTask.DB_TABLE_NAME}"
        db?.execSQL(dropDownloadTaskTAble)

        onCreate(db)
    }

    fun insertTask(task: DownloadTask) {
        val db = this.writableDatabase
        db.transaction {
            addTask(task, db)
        }
    }

    fun getTask(id: String): DownloadTask {
        val db = this.writableDatabase
        val selectQuery = "SELECT * FROM ${DownloadTask.DB_TABLE_NAME} WHERE ${DownloadTask.ID} = :$id"
        val cursor = db.rawQuery(selectQuery, null)
        cursor?.moveToFirst()
        val taskId = cursor.getString(cursor.getColumnIndex(DownloadTask.ID))
        val videoTitle = cursor.getString(cursor.getColumnIndex(DownloadTask.TITLE))
        val coverUrl = cursor.getString(cursor.getColumnIndex(DownloadTask.COVER_URL))
        cursor.close()
        return DownloadTask(
            id = taskId,
            title = videoTitle,
            cover = coverUrl
        )
    }

    fun getAllTasks(): List<DownloadTask> {
        val temp = mutableListOf<DownloadTask>()
        val db = this.writableDatabase
        val selectQuery = "SELECT * FROM ${DownloadTask.DB_TABLE_NAME}"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val taskId = cursor.getString(cursor.getColumnIndex(DownloadTask.ID))
                    val videoTitle = cursor.getString(cursor.getColumnIndex(DownloadTask.TITLE))
                    val coverUrl = cursor.getString(cursor.getColumnIndex(DownloadTask.COVER_URL))
                    temp.add(DownloadTask(
                        id = taskId,
                        title = videoTitle,
                        cover = coverUrl
                    ))
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return temp
    }

    companion object {
        private const val DB_VERSION = 2
        private const val DB_NAME = "ExtractX.db"
        private const val DB_COLUMN_INTEGER = "INTEGER"
        private const val DB_COLUMN_TEXT = "TEXT"
    }
}