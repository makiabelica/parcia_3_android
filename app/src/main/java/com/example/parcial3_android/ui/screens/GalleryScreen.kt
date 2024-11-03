package com.example.parcial3_android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberImagePainter
import com.example.parcial3_android.model.MediaFile


@Composable
fun GalleryScreen(mediaFiles: List<MediaFile>) {
    LazyColumn {
        items(mediaFiles) { mediaFile ->
            when (mediaFile) {
                is MediaFile.Photo -> {
                    Image(
                        painter = rememberImagePainter(mediaFile.uri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is MediaFile.Video -> {
                    // VideoPlayer(mediaFile.uri) // Usa un reproductor de video compatible
                }
            }
        }
    }
}