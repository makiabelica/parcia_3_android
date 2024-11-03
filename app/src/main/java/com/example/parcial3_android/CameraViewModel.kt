package com.example.parcial3_android

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import com.example.parcial3_android.model.MediaFile
import com.example.parcial3_android.service.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

class CameraViewModel(context: Context) : ViewModel()  {
    private val repository = MediaRepository(context)

    private val _mediaFiles = MutableStateFlow<List<MediaFile>>(emptyList())
    val mediaFiles: StateFlow<List<MediaFile>> = _mediaFiles

    private val _flashMode = MutableStateFlow(ImageCapture.FLASH_MODE_OFF)
    val flashMode = _flashMode.asStateFlow()

    fun capturePhoto(uri: Uri) {
        val photo = repository.saveFile(uri, isPhoto = true)
        updateGallery(photo)
    }

    fun recordVideo(uri: Uri) {
        val video = repository.saveFile(uri, isPhoto = false)
        updateGallery(video)
    }

    private fun updateGallery(mediaFile: MediaFile) {
        _mediaFiles.value = _mediaFiles.value + mediaFile
    }

    fun createPhotoFile(): File = repository.createFile(isPhoto = true)
    fun createVideoFile(): File = repository.createFile(isPhoto = false)

    fun addVideoToGallery(context: Context, videoFile: File) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }

        val resolver = context.contentResolver
        val videoUri: Uri? = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        videoUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                copyFileToStream(videoFile, outputStream)
            }
            videoFile.delete() // Elimina el archivo temporal
        }
    }

    fun copyFileToStream(sourceFile: File, outputStream: OutputStream) {
        FileInputStream(sourceFile).use { inputStream ->
            outputStream.use { outStream ->
                inputStream.copyTo(outStream)
            }
        }
    }

    // Función para guardar una foto en la galería
    fun addPhotoToGallery(context: Context, photoFile: File) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, photoFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        photoUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                photoFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            photoFile.delete() // Elimina el archivo temporal
        }
    }

    fun toggleFlashMode() {
        _flashMode.value = when (_flashMode.value) {
            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
            ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_OFF
            else -> ImageCapture.FLASH_MODE_OFF
        }
    }
}