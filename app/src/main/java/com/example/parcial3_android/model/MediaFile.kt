package com.example.parcial3_android.model

import android.net.Uri

sealed class MediaFile(val uri: Uri) {
    class Photo(uri: Uri) : MediaFile(uri)
    class Video(uri: Uri) : MediaFile(uri)
}