package com.example.ui.screens

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.security.UrlSecurityValidator
import com.example.security.UrlValidationResult
import com.example.ui.components.InvalidQrDialog
import com.example.ui.components.UrlConfirmDialog
import com.example.ui.theme.DarkAccentCyan
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
  enforceHttps: Boolean,
  allowedDomains: Set<String>,
  allowAllHttps: Boolean,
  onNavigateBack: () -> Unit,
  onExamConfirmed: (String) -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var camera by remember { mutableStateOf<Camera?>(null) }
  var isTorchOn by remember { mutableStateOf(false) }
  var isScanningActive by remember { mutableStateOf(true) }

  var pendingExamUrl by remember { mutableStateOf<String?>(null) }
  var pendingExamHost by remember { mutableStateOf("") }
  var invalidQrMessage by remember { mutableStateOf<String?>(null) }

  val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

  DisposableEffect(Unit) {
    onDispose {
      cameraExecutor.shutdown()
    }
  }

  // Laser scan line animation
  val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
  val laserProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_progress"
  )

  fun handleScannedRawValue(rawValue: String) {
    if (!isScanningActive) return
    isScanningActive = false

    val result = UrlSecurityValidator.validate(
      input = rawValue,
      enforceHttps = enforceHttps,
      allowedDomains = allowedDomains,
      allowAllHttps = allowAllHttps
    )

    when (result) {
      is UrlValidationResult.Valid -> {
        pendingExamUrl = result.normalizedUrl
        pendingExamHost = result.host
      }
      is UrlValidationResult.Warning -> {
        pendingExamUrl = result.normalizedUrl
        pendingExamHost = result.host
      }
      is UrlValidationResult.Invalid -> {
        invalidQrMessage = result.reason
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("screen_qr_scanner")
  ) {
    // Camera ViewFinder Preview
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { ctx ->
        val previewView = PreviewView(ctx).apply {
          scaleType = PreviewView.ScaleType.FILL_CENTER
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
          val cameraProvider = cameraProviderFuture.get()
          val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
          }

          val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

          val scanner = BarcodeScanning.getClient()

          imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(scanner, imageProxy) { qrValue ->
              previewView.post {
                handleScannedRawValue(qrValue)
              }
            }
          }

          try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
              lifecycleOwner,
              CameraSelector.DEFAULT_BACK_CAMERA,
              preview,
              imageAnalysis
            )
          } catch (e: Exception) {
            // Camera binding failure fallback
          }
        }, ContextCompat.getMainExecutor(ctx))

        previewView
      }
    )

    // Dark Vignette & Bounding Box Overlay
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Bar: Back Button, Title, Flash Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Surface(
          shape = CircleShape,
          color = Color.Black.copy(alpha = 0.6f),
          modifier = Modifier.size(46.dp)
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("button_scanner_back")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Kembali",
              tint = Color.White
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color.Black.copy(alpha = 0.6f)
        ) {
          Text(
            text = "SCAN QR CODE UJIAN",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }

        Surface(
          shape = CircleShape,
          color = Color.Black.copy(alpha = 0.6f),
          modifier = Modifier.size(46.dp)
        ) {
          IconButton(
            onClick = {
              val current = isTorchOn
              camera?.cameraControl?.enableTorch(!current)
              isTorchOn = !current
            },
            modifier = Modifier.testTag("button_scanner_flash")
          ) {
            Icon(
              imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
              contentDescription = "Flash",
              tint = if (isTorchOn) Color(0xFFFFD700) else Color.White
            )
          }
        }
      }

      // Center Scan Frame Box with Corner Brackets & Laser
      Box(
        modifier = Modifier
          .size(270.dp)
          .clip(RoundedCornerShape(24.dp))
          .border(2.dp, DarkAccentCyan.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
          .background(Color.Transparent),
        contentAlignment = Alignment.Center
      ) {
        // Laser Line Animation
        Canvas(modifier = Modifier.fillMaxSize()) {
          val y = size.height * laserProgress
          drawLine(
            color = DarkAccentCyan,
            start = Offset(20f, y),
            end = Offset(size.width - 20f, y),
            strokeWidth = 4f
          )
        }
      }

      // Bottom Guidance Card & Quick Sim button for Emulator environments
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.Black.copy(alpha = 0.75f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Posisikan QR Code di dalam kotak pemindaian",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = Color.White,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Pemindaian berlangsung otomatis. Pastikan pencahayaan cukup.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = Color.White.copy(alpha = 0.75f),
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick simulation chip for testing without physical QR printout
        Button(
          onClick = {
            handleScannedRawValue("https://docs.google.com/forms")
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.2f),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("button_simulate_qr_scan")
        ) {
          Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Simulasikan Scan QR Ujian",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
          )
        }
      }
    }

    // Modal: Confirmation before starting
    pendingExamUrl?.let { url ->
      UrlConfirmDialog(
        url = url,
        host = pendingExamHost,
        onDismiss = {
          pendingExamUrl = null
          isScanningActive = true
        },
        onConfirmStart = {
          val target = url
          pendingExamUrl = null
          onExamConfirmed(target)
        }
      )
    }

    // Modal: Invalid QR Code
    invalidQrMessage?.let { reason ->
      InvalidQrDialog(
        message = reason,
        onDismiss = {
          invalidQrMessage = null
          isScanningActive = true
        }
      )
    }
  }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
  scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
  imageProxy: ImageProxy,
  onBarcodeDetected: (String) -> Unit
) {
  val mediaImage = imageProxy.image
  if (mediaImage != null) {
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(image)
      .addOnSuccessListener { barcodes ->
        for (barcode in barcodes) {
          val raw = barcode.rawValue
          if (!raw.isNullOrBlank()) {
            onBarcodeDetected(raw)
            break
          }
        }
      }
      .addOnCompleteListener {
        imageProxy.close()
      }
  } else {
    imageProxy.close()
  }
}
