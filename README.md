### Funcionalidades Principales

Nota: Video verlo con correo institucional
[Video](https://ufgedu-my.sharepoint.com/:v:/g/personal/ia_angieespinoza_ufg_edu_sv/EYJ04lJCwHxEjCQJ7nKtP_IB9sy5XeDa4_TW5jFtRjDupw?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=udJkcp)

**Captura de Foto**
- Botón de Captura: Permite a los usuarios tomar fotos instantáneamente.
- Almacenamiento: Las fotos se guardan en una carpeta específica dentro de la aplicación para fácil acceso.
- Galería de Fotos: Las fotos capturadas se muestran en una galería de la aplicación, permitiendo una revisión rápida.
- 
**Grabación de Video**
- Botón de Grabación: Permite iniciar y detener la grabación de videos de forma sencilla.
- Confirmación de Guardado: Muestra un mensaje confirmando el guardado del video en la galería de la aplicación.
- Reproducción de Video: Permite revisar los videos grabados desde la misma aplicación.
- 
**Controles de Cámara**
- Zoom y Enfoque: Controles ajustables de zoom y enfoque disponibles en la interfaz.
- Alternar Cámara: Botón para cambiar entre cámara frontal y trasera.
- Flash: Opción de flash para iluminar fotos y videos en entornos con poca luz.
- 
**Interfaz de Usuario**
- Navegación Intuitiva: Los usuarios pueden cambiar fácilmente entre modos de foto, video y configuración.
- Diseño Accesible: Botones y controles diseñados para fácil acceso y manipulación con una sola mano.
- Mensajes de Éxito/Error: Cada operación (captura, grabación o cambio de configuración) muestra un mensaje de confirmación o error.

### Dependencias usadas
```
implementation("androidx.camera:camera-core:1.4.0")
implementation("androidx.camera:camera-camera2:1.4.0")
implementation("androidx.camera:camera-lifecycle:1.4.0")
implementation("androidx.camera:camera-view:1.4.0")
implementation("androidx.media3:media3-ui:1.4.1")
implementation("androidx.media3:media3-exoplayer:1.4.1")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.compose.material3:material3-android:1.3.1")
implementation("androidx.camera:camera-video:1.4.0")
implementation("io.coil-kt:coil-compose:2.3.0")
implementation("androidx.navigation:navigation-compose:2.8.3")
implementation("androidx.compose.material3:material3-android:1.3.1")
implementation("androidx.compose.material:material-icons-extended:1.5.0")
implementation("androidx.compose.ui:ui-android:1.7.5")
```


### Permisos
- **CAMERA:** Necesario para acceder a la cámara del dispositivo.
- **RECORD_AUDIO:** Necesario para capturar audio en las grabaciones de video.
- **WRITE_EXTERNAL_STORAGE** y **READ_EXTERNAL_STORAGE:** Necesarios para almacenar y acceder a fotos y videos capturados en la galería de la aplicación.

Características de Hardware
- android.hardware.camera: Declara que la app utiliza la cámara, pero no la requiere obligatoriamente (required="false"), permitiendo su instalación en dispositivos sin cámara.
