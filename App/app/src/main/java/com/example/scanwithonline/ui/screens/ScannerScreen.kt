package com.example.scanwithonline.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.scanwithonline.R
import com.example.scanwithonline.ui.theme.ScanWithOnlineTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(navController: NavController) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(key1 = true) {
        cameraPermissionState.launchPermissionRequest()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            CameraScanner(navController = navController)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Camera permission is required to use this feature.")
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraScanner(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasScanned by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isFlashlightOn by remember { mutableStateOf(false) }

    // We create and remember the PreviewView here to use it in both the factory and the modifier
    val previewView = remember { PreviewView(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                previewView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = CameraPreview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                                val image = InputImage.fromMediaImage(
                                    imageProxy.image!!,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                val options = BarcodeScannerOptions.Builder()
                                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                                    .build()
                                val scanner = BarcodeScanning.getClient(options)

                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        if (barcodes.isNotEmpty() && !hasScanned) {
                                            hasScanned = true
                                            val barcodeValue = barcodes[0].rawValue
                                            if (barcodeValue != null) {
                                                navController.navigate("details_screen/$barcodeValue")
                                            }
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            }
                        }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        // Store the camera instance for flashlight and focus control
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        // Handle exceptions
                    }
                }, ContextCompat.getMainExecutor(it))
                previewView
            },
            // Add the tap-to-focus modifier
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { offset ->
                    camera?.let {
                        val factory = previewView.meteringPointFactory
                        val point = factory.createPoint(offset.x, offset.y)
                        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
                        it.cameraControl.startFocusAndMetering(action)
                    }
                }
            }
        )

        ScannerOverlayAndControls(
            navController = navController,
            isFlashlightOn = isFlashlightOn,
            hasFlashUnit = camera?.cameraInfo?.hasFlashUnit() ?: false,
            onFlashlightToggle = {
                isFlashlightOn = !isFlashlightOn
                camera?.cameraControl?.enableTorch(isFlashlightOn)
            }
        )
    }
}

@Composable
fun ScannerOverlayAndControls(
    navController: NavController,
    isFlashlightOn: Boolean,
    hasFlashUnit: Boolean,
    onFlashlightToggle: () -> Unit
) {
    val scannerBoxSize = 280.dp
    val verticalOffset = 180.dp // Reset to a more central position

    Box(modifier = Modifier.fillMaxSize()) {
        ScannerOverlay(
            modifier = Modifier.fillMaxSize(),
            boxSize = scannerBoxSize,
            verticalOffset = verticalOffset
        )

        // Flashlight toggle button
        if (hasFlashUnit) {
            IconButton(
                onClick = onFlashlightToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (isFlashlightOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    contentDescription = "Toggle Flashlight",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp), // Added padding to lift it from the edge
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("manual_input_screen") },
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
                      containerColor = Color.Green
                )
            ) {
                Text(
                    text = "Manual Input",
//                    color = MaterialTheme.colorScheme.onPrimary,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Surface(
                modifier = Modifier.size(280.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent
            ) {
                Image(
                    painter = painterResource(id = R.drawable.barcodehelper),
                    contentDescription = "Manual Input Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ScannerOverlay(modifier: Modifier, boxSize: Dp, verticalOffset: Dp) {
    val cornerRadius = 24.dp
    val strokeWidth = 4.dp

    Canvas(modifier = modifier) {
        val verticalOffsetPx = verticalOffset.toPx()
        val boxSizePx = boxSize.toPx()
        val cutoutRect = Rect(
            left = (size.width - boxSizePx) / 2,
            top = (size.height - boxSizePx) / 2 - verticalOffsetPx,
            right = (size.width + boxSizePx) / 2,
            bottom = (size.height + boxSizePx) / 2 - verticalOffsetPx
        )
        drawIntoCanvas { canvas ->
            canvas.withSaveLayer(Rect(Offset.Zero, size), Paint()) {
                drawRect(color = Color.Black.copy(alpha = 0.6f))
                drawRoundRect(
                    topLeft = cutoutRect.topLeft,
                    size = cutoutRect.size,
                    cornerRadius = CornerRadius(cornerRadius.toPx()),
                    color = Color.Transparent,
                    blendMode = BlendMode.Clear
                )
            }
        }
        drawRoundRect(
            topLeft = cutoutRect.topLeft,
            size = cutoutRect.size,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            color = Color.White,
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerScreenPreview() {
    ScanWithOnlineTheme {
        ScannerScreen(navController = rememberNavController())
    }
}
