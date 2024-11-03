package com.example.parcial3_android.ui.components

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parcial3_android.CameraViewModel


@Composable
fun PhotoControls(
    viewModel: CameraViewModel,
    onCapturePhoto: () -> Unit,
    onSwitchCamera: () -> Unit
) {
    val flashMode = viewModel.flashMode.collectAsState().value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Botón de cambio de cámara
        FloatingActionButton(onClick = onSwitchCamera) {
            Icon(Icons.Default.Cameraswitch, contentDescription = "Cambiar cámara")
        }

        // Botón de captura de foto
        FloatingActionButton(onClick = onCapturePhoto) {
            Icon(Icons.Default.Favorite, contentDescription = "Capturar foto")
        }

        // Botón de flash
        FloatingActionButton(onClick = viewModel::toggleFlashMode) {
            val icon = when (flashMode) {
                ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashAuto
                ImageCapture.FLASH_MODE_AUTO -> Icons.Filled.FlashOff
                else -> Icons.Default.FavoriteBorder
            }
            Icon(icon, contentDescription = "Alternar flash")
        }

    }
}