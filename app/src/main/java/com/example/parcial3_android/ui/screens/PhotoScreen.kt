package com.example.parcial3_android.ui.screens

import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.parcial3_android.CameraViewModel
import com.example.parcial3_android.ui.components.PhotoControls

@Composable
fun PhotoScreen(viewModel: CameraViewModel, navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    // Observa el modo de flash para mostrar un Toast cuando está encendido
    val flashMode by viewModel.flashMode.collectAsState()
    LaunchedEffect(flashMode) {
        if (flashMode == ImageCapture.FLASH_MODE_ON) {
            Toast.makeText(context, "Flash encendido", Toast.LENGTH_SHORT).show()
        }
    }

    // Configura la cámara
    fun bindCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            previewView?.let { pv -> it.setSurfaceProvider(pv.surfaceProvider) }
        }

        imageCapture = ImageCapture.Builder()
            .setFlashMode(flashMode) // Aplica el modo de flash aquí
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("PhotoScreen", "Error al vincular la cámara", exc)
            Toast.makeText(context, "Error al vincular la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // Espera a que el cameraProviderFuture esté listo y luego configura la cámara
    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        bindCamera(cameraProvider)
    }

    Box {
        // Vista previa de la cámara
        AndroidView(
            factory = { context ->
                FrameLayout(context).apply {
                    previewView = PreviewView(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                    addView(previewView)
                }
            }
        )

        // Controles de foto posicionados en la parte inferior
        Column(
            modifier = androidx.compose.ui.Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhotoControls(
                viewModel = viewModel,
                onCapturePhoto = {
                    val photoFile = viewModel.createPhotoFile()
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture?.flashMode = flashMode
                    imageCapture?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                viewModel.addPhotoToGallery(context, photoFile)
                                Toast.makeText(context, "Foto guardada en la galería", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("PhotoScreen", "Error al capturar foto: ${exception.message}", exception)
                                Toast.makeText(context, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                onSwitchCamera = {
                    // Cambia el selector de cámara y vuelve a enlazar la cámara
                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }

                    // Solo realiza la operación cuando el cameraProviderFuture esté listo
                    cameraProviderFuture.get()?.let { cameraProvider ->
                        bindCamera(cameraProvider)
                        Toast.makeText(context, "Cámara cambiada", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}
