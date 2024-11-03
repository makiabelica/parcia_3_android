package com.example.parcial3_android.service

import android.content.Context
import android.net.Uri
import com.example.parcial3_android.model.MediaFile
import java.io.File

class MediaRepository(private val context: Context) {
    private val mediaDir: File? = context.externalMediaDirs.firstOrNull()?.let {
        File(it, "MyApp").apply { mkdirs() }
    }

    fun saveFile(uri: Uri, isPhoto: Boolean): MediaFile {
        return if (isPhoto) MediaFile.Photo(uri) else MediaFile.Video(uri)
    }

    fun createFile(isPhoto: Boolean): File {
        val extension = if (isPhoto) ".jpg" else ".mp4"
        return File(mediaDir, "${System.currentTimeMillis()}$extension")
    }
}