package com.example.parcial3_android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial3_android.ui.screens.HomeScreen
import com.example.parcial3_android.ui.screens.PhotoScreen
import com.example.parcial3_android.ui.screens.VideoScreen
import com.example.parcial3_android.ui.theme.Parcial3_androidTheme

class MainActivity : ComponentActivity() {
    private var hasCameraPermission by mutableStateOf(false) // Variable para manejar el estado del permiso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verificar y solicitar el permiso de cámara
        val cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasCameraPermission = isGranted // Actualiza el estado del permiso
        }

        // Solicita el permiso si no está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            Parcial3_androidTheme {
                // Llama a AppNavigation que gestiona permisos y navegación
                AppNavigation(
                    onOpenPhotoGallery = { openPhotoGallery() }
                )
            }
        }
    }
    private fun openPhotoGallery() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        startActivity(intent)
    }

    private fun openVideoGallery() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
        }
        startActivity(intent)
    }
}


@Composable
fun AppNavigation(
    onOpenPhotoGallery: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Estado para verificar si los permisos se han concedido
    var permissionsGranted by remember { mutableStateOf(false) }

    // Solicitar permisos necesarios según la versión de Android
    val permissionsToRequest = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).apply {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    // Solicitar permisos
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions[Manifest.permission.CAMERA] == true &&
                permissions[Manifest.permission.RECORD_AUDIO] == true &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                        (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true))

        Log.d("AppNavigation", "Permisos actualizados: $permissionsGranted")
    }

    // Verificar permisos en el inicio y solicitar permisos si no están concedidos
    LaunchedEffect(Unit) {
        val cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val recordAudioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        val writeStoragePermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) else PackageManager.PERMISSION_GRANTED
        val readStoragePermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) else PackageManager.PERMISSION_GRANTED

        Log.d("AppNavigation", "Verificación de permisos al iniciar:")
        Log.d("AppNavigation", "CAMERA: ${cameraPermission == PackageManager.PERMISSION_GRANTED}")
        Log.d("AppNavigation", "RECORD_AUDIO: ${recordAudioPermission == PackageManager.PERMISSION_GRANTED}")
        Log.d("AppNavigation", "WRITE_EXTERNAL_STORAGE: ${writeStoragePermission == PackageManager.PERMISSION_GRANTED}")
        Log.d("AppNavigation", "READ_EXTERNAL_STORAGE: ${readStoragePermission == PackageManager.PERMISSION_GRANTED}")

        if (cameraPermission == PackageManager.PERMISSION_GRANTED &&
            recordAudioPermission == PackageManager.PERMISSION_GRANTED &&
            writeStoragePermission == PackageManager.PERMISSION_GRANTED &&
            readStoragePermission == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted = true
            Log.d("AppNavigation", "Permisos concedidos inicialmente.")
        } else {
            Log.d("AppNavigation", "Solicitando permisos...")
            launcher.launch(permissionsToRequest)
        }
    }

    // Crear una instancia de CameraViewModel que será compartida entre todas las pantallas
    val viewModel: CameraViewModel = viewModel(factory = CameraViewModelFactory(context))

    // Navegación de la app, solo si los permisos están concedidos
    if (permissionsGranted) {
        Log.d("AppNavigation", "Mostrando navegación.")
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    navController = navController,
                    onOpenPhotoGallery = onOpenPhotoGallery
                )
            }
            composable("photo") {
                PhotoScreen(viewModel = viewModel, navController = navController)
            }
            composable("video") {
                VideoScreen(viewModel = viewModel, navController = navController)
            }
            composable("view_photos") { /* Pantalla para ver las fotos guardadas */ }
            composable("view_videos") { /* Pantalla para ver los videos guardados */ }
        }
    } else {
        // Muestra un mensaje mientras se conceden los permisos
        Text("Se requieren permisos para continuar...")
        Log.d("AppNavigation", "Esperando permisos.")
    }

}