package com.example.parcial3_android.ui.screens

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.parcial3_android.CameraViewModel
import com.example.parcial3_android.ui.components.VideoControls
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VideoScreen(viewModel: CameraViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording: Recording? by remember { mutableStateOf(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var isFlashOn by remember { mutableStateOf(false) } // Estado del flash

    // Función para configurar la cámara con el selector actual
    fun bindCamera(cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HD))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                videoCapture
            )

            // Configurar el estado inicial del flash
            camera.cameraControl.enableTorch(isFlashOn)
        } catch (exc: Exception) {
            Log.e("VideoScreen", "Error al vincular la cámara", exc)
            ContextCompat.getMainExecutor(context).execute {
                Toast.makeText(context, "Error al vincular la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider, previewView) // Llamada inicial a bindCamera

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )

    VideoControls(
        onRecordVideo = { isRecording ->
            if (isRecording) {
                val videoFile = viewModel.createVideoFile()
                val outputOptions = FileOutputOptions.Builder(videoFile).build()

                recording = videoCapture?.output
                    ?.prepareRecording(context, outputOptions)
                    ?.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                        if (recordEvent is VideoRecordEvent.Finalize) {
                            if (!recordEvent.hasError()) {
                                viewModel.addVideoToGallery(context, videoFile)
                                Toast.makeText(context, "Grabación finalizada", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("CameraScreen", "Error en la grabación de video")
                            }
                        }
                    }
            } else {
                recording?.stop()
                recording = null
            }
        },
        onSwitchCamera = {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                Toast.makeText(context, "Cámara Frontal", Toast.LENGTH_SHORT).show()
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                Toast.makeText(context, "Cámara Trasera", Toast.LENGTH_SHORT).show()
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider, PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            })
        },
        onToggleFlash = { flashOn ->
            isFlashOn = flashOn
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector
            )
            camera.cameraControl.enableTorch(isFlashOn)
            val message = if (isFlashOn) "Flash encendido" else "Flash apagado"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    )
}